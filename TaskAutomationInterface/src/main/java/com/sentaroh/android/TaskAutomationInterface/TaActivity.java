package com.sentaroh.android.TaskAutomationInterface;

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

import android.app.Activity;
import android.os.Bundle;

public abstract class TaActivity extends Activity{
	private TaApplicationInterface taskAutoIntf=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TaskAutomationとのインターフェースを初期化
		try {
			taskAutoIntf=new TaApplicationInterface(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//TaskAutomationインターフェースを終了する
		//これは、必ず実行してください。
		//実行しない場合はunregisterReceiverが実行されないため、リソースリークで異常終了します。
		if (taskAutoIntf!=null) taskAutoIntf.destroyInterface();
	}
	final public TaApplicationInterface getTaskAutomationInterface() {
		return taskAutoIntf;
	}
}
