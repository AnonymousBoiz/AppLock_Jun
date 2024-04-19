package com.appanhnt.applocker.activity;

import static androidx.lifecycle.LifecycleOwnerKt.getLifecycleScope;
import static com.appanhnt.applocker.utils.ExtensionKtKt.executeAsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appanhnt.applocker.R;
import com.appanhnt.applocker.adapter.IconCamouflageAdapter;
import com.appanhnt.applocker.interfaces.OnItemIconListener;
import com.appanhnt.applocker.utils.Settings;
import com.appanhnt.applocker.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Job;

public final class IconCamouflageActivity extends AppCompatActivity implements OnItemIconListener {
    private IconCamouflageAdapter mIconCamouflageAdapter;
    public Map<Integer, View> _$_findViewCache = new LinkedHashMap<>();
    private int mIconSelected = R.drawable.ic_default;
    private String mAppName = "default_0";
    private final String[] iconColour = {"default_0", "calculator_0", "calculator_1", "calculator_2", "calculator_3", "office_reader_0", "office_reader_1", "office_reader_2", "office_reader_3"};

    private final void initData() {
    }

    public void _$_clearFindViewByIdCache() {
        this._$_findViewCache.clear();
    }

    public View _$_findCachedViewById(int i) {
        Map<Integer, View> map = this._$_findViewCache;
        View view = map.get(Integer.valueOf(i));
        if (view == null) {
            View findViewById = findViewById(i);
            if (findViewById != null) {
                map.put(Integer.valueOf(i), findViewById);
                return findViewById;
            }
            return null;
        }
        return view;
    }

    public final int getMIconSelected() {
        return this.mIconSelected;
    }

    public final void setMIconSelected(int i) {
        this.mIconSelected = i;
    }

    public final String getMAppName() {
        return this.mAppName;
    }

    public final void setMAppName(String str) {
        this.mAppName = str;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_icon_camouflage);
        initData();
        initView();
        handleEvents();
    }

    private void initView() {
        ((LinearLayout) _$_findCachedViewById(R.id.btnSave)).setEnabled(false);
        if (Settings.Companion.getINSTANCE().getIconAppId() != 0) {
            this.mIconSelected = Settings.Companion.getINSTANCE().getIconAppId();
            Glide.with((FragmentActivity) this).load(Integer.valueOf(Settings.Companion.getINSTANCE().getIconAppId())).placeholder((int) R.drawable.loading).error(R.drawable.loading).into((ImageView) _$_findCachedViewById(R.id.ivCurrentIcon));
        }
        String[] stringArray = getResources().getStringArray(R.array.app_name_array);
        final List list = ArraysKt.toList(stringArray);
        final ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf((int) R.array.calculator_icon_array));
        arrayList.add(Integer.valueOf((int) R.array.office_reader_icon_array));
        arrayList.add(Integer.valueOf((int) R.array.calculator_icon_array));
        executeAsyncTask(getLifecycleScope(this), new Function0<Unit>() {

            public void invoke2() {
            }

            @Override
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }
        }, new Function0<ArrayList<ArrayList<Integer>>>() {
            {
//                super();
            }

            @Override
            public ArrayList<ArrayList<Integer>> invoke() {
                ArrayList<ArrayList<Integer>> arrayList2 = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    ArrayList<Integer> arrayList3 = new ArrayList<>();
                    if (i == 0) {
                        arrayList3.add((int) R.drawable.ic_default);
                    } else {
                        Resources resources = IconCamouflageActivity.this.getResources();
                        Integer num = (Integer) arrayList.get(i - 1);
                        TypedArray obtainTypedArray = resources.obtainTypedArray(num);
                        for (int i2 = 0; i2 < 4; i2++) {
                            arrayList3.add(obtainTypedArray.getResourceId(i2, 0));
                        }
                        obtainTypedArray.recycle();
                    }
                    arrayList2.add(arrayList3);
                }
                return arrayList2;
            }
        }, new Function1<ArrayList<ArrayList<Integer>>, Unit>() {
            {
//                super(1);
            }

            @Override
            public /* bridge */ /* synthetic */ Unit invoke(ArrayList<ArrayList<Integer>> arrayList2) {
                invoke2(arrayList2);
                return Unit.INSTANCE;
            }

            public void invoke2(ArrayList<ArrayList<Integer>> it) {
                IconCamouflageAdapter iconCamouflageAdapter;
                ((RecyclerView) IconCamouflageActivity.this._$_findCachedViewById(R.id.rvIconApp)).setLayoutManager(new LinearLayoutManager(IconCamouflageActivity.this));
                IconCamouflageActivity iconCamouflageActivity = IconCamouflageActivity.this;
                IconCamouflageActivity iconCamouflageActivity2 = IconCamouflageActivity.this;
                iconCamouflageActivity.mIconCamouflageAdapter = new IconCamouflageAdapter(iconCamouflageActivity2, list, it, iconCamouflageActivity2);
                RecyclerView recyclerView = (RecyclerView) IconCamouflageActivity.this._$_findCachedViewById(R.id.rvIconApp);
                iconCamouflageAdapter = IconCamouflageActivity.this.mIconCamouflageAdapter;
                if (iconCamouflageAdapter == null) {
                    iconCamouflageAdapter = null;
                }
                recyclerView.setAdapter(iconCamouflageAdapter);
            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
//        CoroutineScopeKt.cancel$default(LifecycleOwnerKt.getLifecycleScope(this), null, 1, null);
    }

    private void handleEvents() {
        ((ImageView) _$_findCachedViewById(R.id.btnContinue)).setOnClickListener(new View.OnClickListener() { // from class: com.lutech.applock.screen.settings.IconCamouflageActivity$$ExternalSyntheticLambda0
            @Override
            public void onClick(View view) {
                IconCamouflageActivity.handleEvents$lambda$0(IconCamouflageActivity.this, view);
            }
        });
        ((LinearLayout) _$_findCachedViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() { // from class: com.lutech.applock.screen.settings.IconCamouflageActivity$$ExternalSyntheticLambda1
            @Override
            public void onClick(View view) {
                IconCamouflageActivity.handleEvents$lambda$1(IconCamouflageActivity.this, view);
            }
        });
    }

    public static final void handleEvents$lambda$0(IconCamouflageActivity this$0, View view) {
        this$0.finish();
    }

    public static final void handleEvents$lambda$1(IconCamouflageActivity this$0, View view) {
        this$0.showDialog();
    }

    private final void showDialog() {
        final Dialog onCreateBottomSheetDialog$default = Utils.onCreateBottomSheetDialog$default(Utils.INSTANCE, this, R.layout.layout_dialog_change_icon_app, false, 4, null);
        Glide.with((FragmentActivity) this).load(this.mIconSelected).placeholder((int) R.drawable.loading).error(R.drawable.ic_default).into((ImageView) onCreateBottomSheetDialog$default.findViewById(R.id.ivIconApp));
        ((LinearLayout) onCreateBottomSheetDialog$default.findViewById(R.id.btnCancel)).setOnClickListener(view -> IconCamouflageActivity.showDialog$lambda$4$lambda$2(onCreateBottomSheetDialog$default, view));
        ((LinearLayout) onCreateBottomSheetDialog$default.findViewById(R.id.btnRestart)).setOnClickListener(view -> IconCamouflageActivity.showDialog$lambda$4$lambda$3(IconCamouflageActivity.this, onCreateBottomSheetDialog$default, view));
        onCreateBottomSheetDialog$default.show();
    }

    public static final void showDialog$lambda$4$lambda$2(Dialog this_apply, View view) {
        this_apply.dismiss();
    }

    public static final void showDialog$lambda$4$lambda$3(IconCamouflageActivity this$0, Dialog this_apply, View view) {
        Settings.Companion.getINSTANCE().setIconAppId(this$0.mIconSelected);
        this$0.changeIconApp(this$0.mAppName);
        this_apply.dismiss();
    }

    @SuppressLint("WrongConstant")
    private final void changeIconApp(String str) {
        String[] strArr;
        for (String str2 : this.iconColour) {
            int i = Intrinsics.areEqual(str2, str) ? 1 : 2;
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, "com.appanhnt.applocker.activity.SplashActivity1"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, "com.appanhnt.applocker.activity.SplashActivity2"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public void onItemIconClick(int i, String appName) {
        int i2;
        this.mIconSelected = i;
        this.mAppName = appName;
        IconCamouflageAdapter iconCamouflageAdapter = this.mIconCamouflageAdapter;
        if (iconCamouflageAdapter == null) {
            iconCamouflageAdapter = null;
        }
        iconCamouflageAdapter.notifyDataSetChanged();
        if (this.mIconSelected == Settings.Companion.getINSTANCE().getIconAppId() || (this.mIconSelected == R.drawable.ic_default && Settings.Companion.getINSTANCE().getIconAppId() == 0)) {
            ((LinearLayout) Objects.requireNonNull(_$_findCachedViewById(R.id.btnSave))).setEnabled(false);
            i2 = R.drawable.bg_icon_empty;
        } else {
            ((LinearLayout) Objects.requireNonNull(_$_findCachedViewById(R.id.btnSave))).setEnabled(true);
            i2 = this.mIconSelected;
        }
        Glide.with((FragmentActivity) this).load(i2).placeholder((int) R.drawable.loading).error(R.drawable.loading).into((ImageView) Objects.requireNonNull(_$_findCachedViewById(R.id.ivTargetIcon)));
    }
}