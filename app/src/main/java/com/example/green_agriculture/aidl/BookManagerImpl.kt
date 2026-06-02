package com.example.green_agriculture.aidl

import android.os.RemoteCallbackList
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class BookManagerImpl : IBookManager.Stub() {
    private val mBookList = CopyOnWriteArrayList<Book>()
    private val mServiceDestroyed = AtomicBoolean(false)
    private val mListener = RemoteCallbackList<IOnNewBookArrivedListener>()

    override fun getBookList(): List<Book?> {
        return mBookList
    }

    override fun addBook(book: Book?) {
        mBookList.add(book)
    }

    override fun register(listener: IOnNewBookArrivedListener?) {
        mListener.register(listener)
    }

    override fun unregister(listener: IOnNewBookArrivedListener?) {
        mListener.unregister(listener)
    }

    private fun onNewBookArrived(book: Book) {
        addBook(book)

        val listenerCount = mListener.beginBroadcast()
        for (i in 0 until listenerCount) {
            val listener = mListener.getBroadcastItem(i)

            listener.onNewBookArrived(book)
        }

        mListener.finishBroadcast()
    }


    private val runnableTask = Runnable {
        while (!mServiceDestroyed.get()) {
            Thread.sleep(5000)

            val bookId = mBookList.size
            val book = Book(bookName = "New Book #$bookId", bookId = bookId)
            onNewBookArrived(book)
        }
    }


    init {
        addBook(Book(bookName = "Android", bookId = 0))
        addBook(Book(bookName = "Ios", bookId = 1))

        Thread(runnableTask).start()
    }
}