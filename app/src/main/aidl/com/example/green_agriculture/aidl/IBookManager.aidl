// IBookManager.aidl
package com.example.green_agriculture.aidl;

import com.example.green_agriculture.aidl.Book;
import com.example.green_agriculture.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();

    void addBook(in Book book);

    void register(IOnNewBookArrivedListener listener);

    void unregister(IOnNewBookArrivedListener listener);
}