/*
 * Copyright (C) 2012-2013 Jorrit "Chainfire" Jongma
 * Modifications Copyright (C) 2013 Alexander "Evisceration" Martinz
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

package eu.chainfire.libsuperuser;

import android.app.AlarmManager;
import android.content.Context;
import android.os.AsyncTask;

import org.namelessrom.devicecontrol.R;
import org.namelessrom.devicecontrol.utils.PreferenceHelper;

import static org.namelessrom.devicecontrol.utils.constants.DeviceConstants.DC_FIRST_START;
import static org.namelessrom.devicecontrol.utils.Utils.logDebug;

public class Application extends android.app.Application {

    // Switch to your needs
    public static final boolean IS_LOG_DEBUG = false;

    public static boolean IS_SYSTEM_APP = false;
    public static boolean HAS_ROOT = false;

    public static AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        IS_SYSTEM_APP = getResources().getBoolean(R.bool.is_system_app);

        // we need to detect SU for some features :)
        new DetectSu().execute();

        try {
            // workaround bug in AsyncTask, can show up (for example) when you toast from a service
            // this makes sure AsyncTask's internal handler is created from the right (main) thread
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            logDebug("Application: " + e.getMessage(), IS_LOG_DEBUG);
        }
    }

    private class DetectSu extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HAS_ROOT = Shell.SU.available();
            PreferenceHelper.setBoolean(DC_FIRST_START, false);
            return null;
        }
    }
}