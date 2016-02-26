package com.aptoide.amethyst;

import com.aptoide.amethyst.R;

import java.util.HashMap;

/**
 * Created by fabio on 07-10-2015.
 */
public class EnumRollbackState {
    public static HashMap<String, Integer> states = new HashMap<String, Integer>();

    static{
        states.put("Installed", R.string.rollback_installed);
        states.put("Uninstalled", R.string.rollback_uninstalled);
        states.put("Updated", R.string.rollback_updated);
        states.put("Downgraded", R.string.rollback_downgraded);
    }
}
