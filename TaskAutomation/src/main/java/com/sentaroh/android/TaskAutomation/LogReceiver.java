package com.sentaroh.android.TaskAutomation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.sentaroh.android.TaskAutomation.Log.LogUtil;
import com.sentaroh.android.Utilities.CommonGlobalParms;
import com.sentaroh.android.Utilities.LogUtil.CommonLogWriter;
import com.sentaroh.android.Utilities.StringUtil;

import static com.sentaroh.android.TaskAutomation.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_DELETE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_FLUSH;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_RESET;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_ROTATE;
import static com.sentaroh.android.TaskAutomation.Log.LogConstants.BROADCAST_LOG_SEND;

public class LogReceiver extends BroadcastReceiver {

//    private static LogUtil mLogUtil=null;
    private static GlobalParameters mGp=null;
    @Override
    final public void onReceive(Context c, Intent intent) {
        if (mGp==null) {
            mGp=GlobalWorkArea.getGlobalParameters(c);
//            mLogUtil=new LogUtil(c, "LogRcv", mGp);
        }
        String action=intent.getAction();

        if (action.equals(BROADCAST_LOG_SEND)) {
            String type=intent.getStringExtra("TYPE");
            String id=intent.getStringExtra("ID");
            String cat=intent.getStringExtra("CAT");
//            Log.v("TaskAutomation","type="+type+", id="+id+", cat="+cat);
            String log_msg=intent.getStringExtra("MSG");
            StringBuilder print_msg=new StringBuilder(512);
            if (type.equalsIgnoreCase("M")) {
                print_msg
                        .append("M ")
                        .append(cat)
                        .append(" ")
                        .append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
                        .append(" ")
                        .append(id)
                        .append(log_msg);
            } else if (type.equalsIgnoreCase("D")) {
                print_msg
                        .append("D ")
                        .append(cat)
                        .append(" ")
                        .append(StringUtil.convDateTimeTo_YearMonthDayHourMinSecMili(System.currentTimeMillis()))
                        .append(" ")
                        .append(id)
                        .append(log_msg);
            }
            CommonLogWriter.enqueue(mGp, c, mGp.getLogIntentSend(), print_msg.toString(), false);
            Log.v(APPLICATION_TAG,cat+" "+id+log_msg.toString());

        } else if (action.equals(BROADCAST_LOG_RESET)) {
            CommonLogWriter.enqueue(mGp, c, mGp.getLogIntentReset(), "",  true);
        } else if (action.equals(BROADCAST_LOG_ROTATE)) {
            CommonLogWriter.enqueue(mGp, c, mGp.getLogIntentRotate(), "",  true);
        } else if (action.equals(BROADCAST_LOG_DELETE)) {
            CommonLogWriter.enqueue(mGp, c, mGp.getLogIntentDelete(), "",  true);
        } else if (action.equals(BROADCAST_LOG_FLUSH)) {
            CommonLogWriter.enqueue(mGp, c, mGp.getLogIntentFlush(), "",  true);
        }


    };

}
