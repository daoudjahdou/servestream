/*
 * ServeStream: A HTTP stream browser/player for Android
 * Copyright 2010 William Seemann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.servestream.alarm;

import java.util.ArrayList;

import net.sourceforge.servestream.dbutils.Stream;
import net.sourceforge.servestream.dbutils.StreamDatabase;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * The RingtonePreference does not have a way to get/set the current ringtone so
 * we override onSaveRingtone and onRestoreRingtone to get the same behavior.
 */
public class AlarmPreference extends ListPreference {

	private int [] mIds;
	private String [] mNicknames;
	
    // Initial value that can be set with the values saved in the database.
    private int mId = 0;
    // New value that will be set if a positive result comes back from the
    // dialog.
    private int mNewId = -1;
	
	protected StreamDatabase mStreamdb = null;
    
	public AlarmPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
        
		// connect with streams database and populate list
		mStreamdb = new StreamDatabase(getContext());
		
		ArrayList<Stream> streams = mStreamdb.getStreams();

		mStreamdb.close();
		
		String [] entries = new String[(streams.size() + 1)];
		String [] entryValues = new String[(streams.size() + 1)];
		mIds = new int[streams.size() + 1];
		mNicknames = new String[streams.size() + 1];
		
		entries[0] = "Silent";
		entryValues[0] = String.valueOf("0");
		mIds[0] = 0;
		mNicknames[0] = "Silent";
		
		for (int i = 0; i < streams.size(); i++) {
			entries[i + 1] = streams.get(i).getNickname();
			entryValues[i + 1] = String.valueOf(i + 1);
			mIds[i + 1] = (int) streams.get(i).getId();
			mNicknames[i + 1] = streams.get(i).getNickname();
		}
		
        setEntries(entries);
        setEntryValues(entryValues);
	}
 
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mId = mNewId;
            setSummary(mNicknames[mId]);
            callChangeListener(mId);
        }
    }
	
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {	
        CharSequence[] entries = getEntries();
        
        builder.setSingleChoiceItems(
        		entries,
        		mId,
                new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mNewId = which;
					}
				});
	}

	public void setAlertId(int id) {
		for (int i = 0; i < mIds.length; i++) {
			if (mIds[i] == id) {
				mId = i;
				mNewId = i;
	            setSummary(mNicknames[mId]);
			}
		}
	}
	
    public int getAlertId() {
        return mIds[mId];
    }
}