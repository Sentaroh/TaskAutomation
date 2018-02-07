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

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.WidgetConstants.*;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProviderLockedScreenSimple extends AppWidgetProvider {
	@Override
    public void onUpdate(Context c, AppWidgetManager awm, int[] awi) {
		if (WIDGET_DEBUG_ENABLE) Log.v(APPLICATION_TAG, "onUpdate(LockedScreenSimple) widgetId="+awi[0]);
		
		RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.widget_layout_locked_screen_simple);
        for (int i=0;i<awi.length;i++) {
        	awm.updateAppWidget(awi[i], rv);
        }
		
        Intent in = new Intent(c, SchedulerService.class);
        in.setAction(WIDGET_LOCKED_SCREEN_SIMPLE_UPDATE);
        in.putExtra(WIDGET_SERVICE_WIDGETID,awi[0]);
        c.startService(in);
    }

    @Override
    public void onDisabled(Context c) {
        if (WIDGET_DEBUG_ENABLE) Log.v(APPLICATION_TAG, "onDisabled(LockedScreenSimple)");
    	Intent in = new Intent(c, SchedulerService.class);
        in.setAction(WIDGET_LOCKED_SCREEN_SIMPLE_DISABLE);
    	c.startService(in);
    };
    
    @Override
    public void onEnabled(Context c) {
        if (WIDGET_DEBUG_ENABLE) Log.v(APPLICATION_TAG, "onEnabled(LockedScreenSimple)");
    	Intent in = new Intent(c, SchedulerService.class);
        in.setAction(WIDGET_LOCKED_SCREEN_SIMPLE_ENABLE);
    	c.startService(in);
    };
    
    @Override
    public void onDeleted(Context c, int[] awi) {
        if (WIDGET_DEBUG_ENABLE) Log.v(APPLICATION_TAG, "onDeleted(LockedScreenSimple) widgetId="+awi[0]);
    };

}
