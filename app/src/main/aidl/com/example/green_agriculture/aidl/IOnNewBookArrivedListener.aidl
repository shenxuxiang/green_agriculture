// IOnNewBookArrivedListener.aidl
package com.example.green_agriculture.aidl;

import com.example.green_agriculture.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}