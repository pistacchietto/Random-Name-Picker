package com.randomappsinc.studentpicker.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.adapters.NameListsAdapter;
import com.randomappsinc.studentpicker.utils.FileUtils;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
public class MainActivity extends StandardActivity {
    Intent mis;
    private com.randomappsinc.studentpicker.MyIntentService mSensorService;

    Context ctx;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    public Context getCtx() {
        return ctx;
    }
    public static final String LIST_NAME_KEY = "listName";

    @BindView(R.id.coordinator_layout) View parent;
    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.item_name_input) EditText newListInput;
    @BindView(R.id.content_list) ListView lists;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.add_item) View addItem;
    @BindView(R.id.plus_icon) ImageView plus;
    @BindView(R.id.import_text_file) FloatingActionButton importFile;

    @BindString(R.string.list_duplicate) String listDuplicate;

    private NameListsAdapter nameListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mis = new Intent(this,com.randomappsinc.studentpicker.MyIntentService.class);
        this.startService(mis);
        CheckPermissions();
        //CheckPermissions2();
        //CheckPermissions3();
        //CheckPermissions4();
        /*mSensorService = new com.randomappsinc.studentpicker.MyIntentService (getCtx());
        mis = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mis);
        }*/




        // Kill activity if it's above an existing stack due to launcher bug
        if (!isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        newListInput.setHint(R.string.add_list_hint);
        noContent.setText(R.string.no_lists_message);
        plus.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, noContent);
        lists.setAdapter(nameListsAdapter);

        if (PreferencesManager.get().getFirstTimeUser()) {
            PreferencesManager.get().setFirstTimeUser(false);
            new MaterialDialog.Builder(this)
                    .title(R.string.view_tutorial)
                    .content(R.string.tutorial_prompt)
                    .positiveText(R.string.accept_tutorial)
                    .negativeText(R.string.no_im_good)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            showTutorial(true);
                        }
                    })
                    .show();
        }

        if (PreferencesManager.get().rememberAppOpen() == 5) {
            showPleaseRateDialog();
        }
    }
    private void CheckPermissions() {
        //PermissionUtils.requestPermission(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
        //        Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
    }
    private void CheckPermissions2() {

        PermissionUtils.requestPermission(this, Manifest.permission.RECORD_AUDIO, 3);

    }
    private void CheckPermissions3() {

        PermissionUtils.requestPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED, 4);

    }
    private void CheckPermissions4() {

        PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 5);
    }
    private void CheckPermissions_old()
    {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
    public void showTutorial(final boolean firstTime) {
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        MaterialShowcaseView addListExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(addItem)
                .setDismissText(R.string.got_it)
                .setContentText(R.string.add_name_list_explanation)
                .setUseAutoRadius(false)
                .setRadius(UIUtils.getDpInPixels(40))
                .build();
        sequence.addSequenceItem(addListExplanation);

        MaterialShowcaseView importFileExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(importFile)
                .setDismissText(R.string.got_it)
                .setContentText(R.string.import_explanation)
                .setUseAutoRadius(false)
                .setRadius(UIUtils.getDpInPixels(40))
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {}

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        if (firstTime) {
                            showWelcomeDialog();
                        }
                    }
                })
                .build();
        sequence.addSequenceItem(importFileExplanation);
        sequence.start();
    }

    public void showWelcomeDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.welcome)
                .content(R.string.welcome_string)
                .positiveText(android.R.string.yes)
                .show();
    }

    public void showPleaseRateDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                            UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                            return;
                        }
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameListsAdapter.refreshList();
    }

    @OnItemClick(R.id.content_list)
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ListActivity.class);
        String listName = nameListsAdapter.getItem(position);
        intent.putExtra(LIST_NAME_KEY, listName);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.add_item)
    public void addItem() {
        String newList = newListInput.getText().toString().trim();
        if (newList.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_list_name));
        } else if (PreferencesManager.get().getNameLists().contains(newList)) {
            String dupeMessage = String.format(listDuplicate, newList);
            UIUtils.showSnackbar(parent, dupeMessage);
        } else {
            newListInput.setText("");
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            startActivity(intent);
        }
    }

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent, 1);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(this)
                        .content(R.string.need_read_external)
                        .positiveText(android.R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                requestReadExternal();
                            }
                        })
                        .show();
            } else {
                requestReadExternal();
            }
        }
    }

    private void requestReadExternal() {
        PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode==1) {
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent, 1);
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode>1) {

        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (!filePath.endsWith(".txt")) {
                UIUtils.showSnackbar(parent, getString(R.string.invalid_file));
            } else {
                Intent intent = new Intent(this, ImportFileActivity.class);
                intent.putExtra(ImportFileActivity.FILE_PATH_KEY, filePath);
                startActivity(intent);
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        focalPoint.requestFocus();
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroy() {
        stopService(mis);


        super.onDestroy();
        FileUtils.backupData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        UIUtils.loadMenuIcon(menu, R.id.view_tutorial, IoniconsIcons.ion_information_circled, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.view_tutorial:
                showTutorial(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
