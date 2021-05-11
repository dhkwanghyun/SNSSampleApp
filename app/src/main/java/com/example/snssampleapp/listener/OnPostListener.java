package com.example.snssampleapp.listener;

import com.example.snssampleapp.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
