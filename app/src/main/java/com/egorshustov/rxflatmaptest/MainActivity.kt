package com.egorshustov.rxflatmaptest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.egorshustov.rxflatmaptest.data.Post
import com.egorshustov.rxflatmaptest.data.remote.ServiceGenerator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val postsAdapter = PostsAdapter()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

        val disposable = getPostsObservable()
            .subscribeOn(Schedulers.io())
            .concatMap {
                getCommentsObservable(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                postsAdapter.updatePost(it)
            }

        compositeDisposable.add(disposable)
    }

    private fun initRecyclerView() {
        recycler_posts.layoutManager = LinearLayoutManager(this)
        recycler_posts.adapter = postsAdapter
    }

    private fun getPostsObservable(): Observable<Post> {
        return ServiceGenerator.getRequestApi()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                postsAdapter.setPosts(it)
                Observable.fromIterable(it)
                    .subscribeOn(Schedulers.io())
            }
    }

    private fun getCommentsObservable(post: Post): Observable<Post> {
        return ServiceGenerator.getRequestApi()
            .getComments(post.id)
            .map {
                val delay = (Random().nextInt(5) + 1) * 1000 // sleep thread for x ms
                Thread.sleep(delay.toLong())
                Log.d(TAG, "apply: sleeping thread " + Thread.currentThread().name + " for " + delay.toString() + "ms")
                post.comments = it
                post
            }
            .subscribeOn(Schedulers.io())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
