package com.sentaroh.android.TaskAutomation;
/*
The MIT License (MIT)
Copyright (c) 2011-2013 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal 
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to 
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

import android.content.Context;

import com.sentaroh.android.TaskAutomation.R;

public class TaskExecutorMessages {
//	Messages for TaskExecutor    	
    public String msgs_thread_task_started=null;
    public String msgs_thread_task_exec_builtin;
    public String msgs_thread_task_exec_builtin_abort;
    public String msgs_thread_task_unknoww_action;
    public String msgs_thread_task_end_success;
    public String msgs_thread_task_end_cancelled;
    public String msgs_thread_task_end_error;
    public String msgs_thread_task_exec_android;
    public String msgs_thread_task_intent_notfound;
    public String msgs_thread_task_exec_play_sound;
    public String msgs_thread_task_play_sound_notfound;
    public String msgs_thread_task_play_sound_error;
    public String msgs_thread_task_play_ringtone_notfound;
    public String msgs_thread_task_exec_play_ringtone;
    public String msgs_thread_task_screen_lock_ignored;
    public String msgs_thread_task_exec_compare;
    public String msgs_thread_task_exec_compare_abort;
    public String msgs_thread_task_exec_compare_skip;
    public String msgs_thread_task_exec_light_not_available;
    public String msgs_thread_task_exec_proximity_not_available;
    public String msgs_thread_task_exec_magnetic_field_not_available;
    public String msgs_thread_task_exec_message;
    public String msgs_thread_task_exec_time;
    public String msgs_thread_task_exec_task;
    public String msgs_thread_task_exec_wait;
    public String msgs_thread_task_exec_ignore_sound_ringer_not_normal;

    final public void loadString(Context context) {
    	msgs_thread_task_exec_ignore_sound_ringer_not_normal=context.getString(R.string.msgs_thread_task_exec_ignore_sound_ringer_not_normal);
    	msgs_thread_task_started=context.getString(R.string.msgs_thread_task_started);
    	msgs_thread_task_exec_builtin=context.getString(R.string.msgs_thread_task_exec_builtin);
    	msgs_thread_task_exec_builtin_abort=context.getString(R.string.msgs_thread_task_exec_builtin_abort);
    	msgs_thread_task_unknoww_action=context.getString(R.string.msgs_thread_task_unknoww_action);
    	msgs_thread_task_end_success=context.getString(R.string.msgs_thread_task_end_success);
    	msgs_thread_task_end_cancelled=context.getString(R.string.msgs_thread_task_end_cancelled);
    	msgs_thread_task_end_error=context.getString(R.string.msgs_thread_task_end_error);
    	msgs_thread_task_exec_android=context.getString(R.string.msgs_thread_task_exec_android);
    	msgs_thread_task_intent_notfound=context.getString(R.string.msgs_thread_task_intent_notfound);
    	msgs_thread_task_exec_play_sound=context.getString(R.string.msgs_thread_task_exec_play_sound);
    	msgs_thread_task_play_sound_notfound=context.getString(R.string.msgs_thread_task_play_sound_notfound);
    	msgs_thread_task_play_sound_error=context.getString(R.string.msgs_thread_task_play_sound_error);
    	msgs_thread_task_play_ringtone_notfound=context.getString(R.string.msgs_thread_task_play_ringtone_notfound);
    	msgs_thread_task_exec_play_ringtone=context.getString(R.string.msgs_thread_task_exec_play_ringtone);
    	msgs_thread_task_screen_lock_ignored=context.getString(R.string.msgs_thread_task_screen_lock_ignored);
    	msgs_thread_task_exec_compare=context.getString(R.string.msgs_thread_task_exec_compare);
    	msgs_thread_task_exec_compare_abort=context.getString(R.string.msgs_thread_task_exec_compare_abort);
    	msgs_thread_task_exec_compare_skip=context.getString(R.string.msgs_thread_task_exec_compare_skip);
    	msgs_thread_task_exec_light_not_available=context.getString(R.string.msgs_thread_task_exec_light_not_available);
    	msgs_thread_task_exec_proximity_not_available=context.getString(R.string.msgs_thread_task_exec_proximity_not_available);
    	msgs_thread_task_exec_magnetic_field_not_available=context.getString(R.string.msgs_thread_task_exec_magnetic_field_not_available);
    	msgs_thread_task_exec_message=context.getString(R.string.msgs_thread_task_exec_message);
    	msgs_thread_task_exec_time=context.getString(R.string.msgs_thread_task_exec_time);
    	msgs_thread_task_exec_task=context.getString(R.string.msgs_thread_task_exec_task);
    	msgs_thread_task_exec_wait=context.getString(R.string.msgs_thread_task_exec_wait);
    	
    };
}
