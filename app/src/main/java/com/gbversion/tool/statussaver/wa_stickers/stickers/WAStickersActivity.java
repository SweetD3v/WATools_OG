/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.gbversion.tool.statussaver.wa_stickers.stickers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gbversion.tool.statussaver.R;
import com.gbversion.tool.statussaver.utils.AsyncTaskRunner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

public class WAStickersActivity extends BaseActivity {
    private View progressBar;
    private LoadListAsyncTask loadListAsyncTask;
    public static Pair<String, ArrayList<StickerPack>> stringListPair;

    public static final String SHOW_STICKERS_LIST = "show_stickers_list";

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG", "onReceived: ");
            loadStickers();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        overridePendingTransition(0, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.entry_activity_progress);
        Log.e("TAG", "onCreateSt: ");
        registerReceiver(listener, new IntentFilter(SHOW_STICKERS_LIST));

//        loadListAsyncTask = new LoadListAsyncTask(this);
//        loadListAsyncTask.execute();
    }

    private void loadStickers() {
        new AsyncTaskRunner<Void, Pair<String, ArrayList<StickerPack>>>(this) {

            @NonNull
            @Override
            public Pair<String, ArrayList<StickerPack>> doInBackground(@Nullable Void unused) {
                ArrayList<StickerPack> stickerPackList;
                try {
                    try {
                        stickerPackList = StickerPackLoader.fetchStickerPacks(WAStickersActivity.this);
                        if (stickerPackList.size() == 0) {
                            return new Pair<>("could not find any packs", null);
                        }
                        for (StickerPack stickerPack : stickerPackList) {
                            StickerPackValidator.verifyStickerPackValidity(WAStickersActivity.this, stickerPack);
                        }
                        return new Pair<>(null, stickerPackList);
                    } catch (Exception e) {
                        return new Pair<>("could not fetch sticker packs", null);
                    }
                } catch (Exception e) {
                    Log.e("EntryActivity", "error fetching sticker packs", e);
                    return new Pair<>(e.getMessage(), null);
                }
            }

            @Override
            public void onPostExecute(@Nullable Pair<String, ArrayList<StickerPack>> stringArrayListPair) {
                super.onPostExecute(stringArrayListPair);

                stringListPair = stringArrayListPair;
                if (stringArrayListPair != null) {
                    if (stringArrayListPair.first != null) {
                        showErrorMessage(stringArrayListPair.first);
                    } else {
                        showStickerPack(stringArrayListPair.second);
                    }
                } else {
                    Log.e("EntryActivity", "error fetching sticker packs");
                }
            }
        }.execute(null, false);
    }

    private void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        progressBar.setVisibility(View.GONE);
        if (stickerPackList.size() > 1) {
            Log.e("TAG", "showStickerPack: " + stickerPackList.size());
            final Intent intent = new Intent(this, StickerPackListActivity.class);
            intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
            startActivity(intent);
        } else {
            final Intent intent = new Intent(this, StickerPackDetailsActivity.class);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPackList.get(0));
            startActivity(intent);
        }
        onBackPressed();
        overridePendingTransition(0, 0);
    }

    private void showErrorMessage(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e("EntryActivity", "error fetching sticker packs, " + errorMessage);
        final TextView errorMessageTV = findViewById(R.id.error_message);
        errorMessageTV.setText(getString(R.string.error_message, errorMessage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(listener);
        if (loadListAsyncTask != null && !loadListAsyncTask.isCancelled()) {
            loadListAsyncTask.cancel(true);
        }
    }

    static class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<WAStickersActivity> contextWeakReference;

        LoadListAsyncTask(WAStickersActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Pair<String, ArrayList<StickerPack>> doInBackground(Void... voids) {
            ArrayList<StickerPack> stickerPackList;
            try {
                final Context context = contextWeakReference.get();
                if (context != null) {
                    stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList.size() == 0) {
                        return new Pair<>("could not find any packs", null);
                    }
                    for (StickerPack stickerPack : stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
                    }
                    return new Pair<>(null, stickerPackList);
                } else {
                    return new Pair<>("could not fetch sticker packs", null);
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "error fetching sticker packs", e);
                return new Pair<>(e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(Pair<String, ArrayList<StickerPack>> stringListPair) {

            final WAStickersActivity stickersActivity = contextWeakReference.get();
            if (stickersActivity != null) {
                if (stringListPair.first != null) {
                    stickersActivity.showErrorMessage(stringListPair.first);
                } else {
                    WAStickersActivity.stringListPair = stringListPair;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
