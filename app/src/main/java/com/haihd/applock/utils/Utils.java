package com.haihd.applock.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import kotlin.jvm.internal.Intrinsics;

public final class Utils {
    public static final Utils INSTANCE = new Utils();
    private static boolean onShow;

    private Utils() {
    }

    public final boolean getOnShow() {
        return onShow;
    }

    public final void setOnShow(boolean z) {
        onShow = z;
    }

    public static /* synthetic */ Dialog onCreateBottomSheetDialog$default(Utils utils, Context context, int i, boolean z, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            z = true;
        }
        return utils.onCreateBottomSheetDialog(context, i, z);
    }

    public final Dialog onCreateBottomSheetDialog(Context context, int i, boolean z) {
        Intrinsics.checkNotNull(context);
        Dialog dialog = new Dialog(context);
        dialog.setContentView(i);
        dialog.setCanceledOnTouchOutside(z);
        Window window = dialog.getWindow();
        Intrinsics.checkNotNull(window);
        window.setLayout(-1, -2);
        Window window2 = dialog.getWindow();
        Intrinsics.checkNotNull(window2);
        window2.setGravity(80);
        Window window3 = dialog.getWindow();
        Intrinsics.checkNotNull(window3);
        window3.setBackgroundDrawable(new ColorDrawable(0));
        return dialog;
    }


    public final Dialog onCreateBottomSheetDialogMacthParent(Context context, int i, boolean z) {
        Intrinsics.checkNotNull(context);
        Dialog dialog = new Dialog(context);
        dialog.setContentView(i);
        dialog.setCanceledOnTouchOutside(z);
        Window window = dialog.getWindow();
        Intrinsics.checkNotNull(window);
        window.setLayout(-1, -1);
        Window window2 = dialog.getWindow();
        Intrinsics.checkNotNull(window2);
        window2.setBackgroundDrawable(new ColorDrawable(0));
        return dialog;
    }
}