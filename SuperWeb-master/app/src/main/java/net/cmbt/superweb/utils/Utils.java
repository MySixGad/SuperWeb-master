package net.cmbt.superweb.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;

/**
 * Created by LG on 2018/12/13.
 */

public class Utils {


    /**
     * 仿IOS下部弹出效果
     *
     * @param mContext
     * @param mTitle
     * @param mItems
     * @param mListener
     */
    public static void ActionSheetDialog(Context mContext, String mTitle, final String[] mItems, final onActionSheetDialogListener mListener) {

        final ActionSheetDialog dialog = new ActionSheetDialog(mContext, mItems, null);
        dialog.isTitleShow(!(mTitle == null || mTitle.length() == 0));
        dialog.title(mTitle)//
                .titleTextSize_SP(14.5f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                if (mListener != null) {
                    mListener.onClick(position, mItems[position], id);
                }
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }


    public interface onActionSheetDialogListener {
        public void onClick(int position, String mS, long id);
        public void onDismiss(DialogInterface dialogInterface);
    }


}
