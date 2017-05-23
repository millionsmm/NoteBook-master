package com.aoliao.notebook.utils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;




public class FirHelper {
    private BroadcastReceiver receiver;
    private Context context;
    public static FirHelper helper;

    private FirHelper() {
    }

    public static FirHelper getInstance() {
        if (helper == null) {
            helper = new FirHelper();
        }
        return helper;
    }

//    public void checkUpdate(Context context) {
//        this.context = context;
//        addBroadcast();
//        FIR.checkForUpdateInFIR("09ec021f5e836152f27b896d88ebded2", new VersionCheckCallback() {
//            @Override
//            public void onSuccess(String s) {
//                UpdateEntity updateEntity = new Gson().fromJson(s, UpdateEntity.class);
//                String oldVersion = getVersionName(App.getAppContext());
//                String newVersion = updateEntity.getVersionShort();
//                if (!oldVersion.equals(newVersion)) {
//                    Intent intent = new Intent("io.xujiaji.hnbc.update");
//                    intent.putExtra("update_entity", updateEntity);
//                    App.getAppContext().sendBroadcast(intent);
//                }
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                Log.i("fir", "获取更新失败" + "\n" + e.getMessage());
//            }
//
//            @Override
//            public void onStart() {
//                Log.i("fir", "正在获取更新");
//            }
//
//            @Override
//            public void onFinish() {
//                Log.i("fir", "成功获取更新");
//            }
//        });
//    }

//    public static void addCustomizeValue(String key, String value) {
//        FIR.addCustomizeValue(key, value);
//    }

//    public static void removeCustomizeValue(String key) {
//        FIR.removeCustomizeValue(key);
//    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }


//    private void addBroadcast() {
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if ("io.xujiaji.hnbc.update".equals(action)) {
//                    UpdateEntity updateEntity = intent.getParcelableExtra("update_entity");
//                    showUpdateDialog("更新内容：" + updateEntity.getChangelog()
//                            + "\n版　　本：" + updateEntity.getVersionShort()
//                            + "\n大　　小：" + String.format("%.2f M", (updateEntity.getBinary().getFsize()) / (1000.0 * 1000.0)), updateEntity.getInstallUrl());
//                } else if ("io.xujiaji.hnbc.update_progress".equals(action)) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setDataAndType(Uri.fromFile(new File(intent.getStringExtra(UpdateService.APK_LOCAL))), "application/vnd.android.package-archive");
//                    context.startActivity(i);
//                }
//            }
//        };
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("io.xujiaji.hnbc.update");
//        filter.addAction("io.xujiaji.hnbc.update_progress");
//        context.registerReceiver(receiver, filter);
//    }

//    private void showUpdateDialog(String msg, final String url) {
//        AlertDialog dialog = new AlertDialog.Builder(context)
//                .setTitle("检查到更新")
//                .setIcon(R.drawable.ic_update)
//                .setMessage(msg)
//                .setNegativeButton("一会儿吧", null)
//                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        UpdateService.start(url);
//                        show(context);
//                    }
//                })
//                .create();
//        dialog.show();
//    }

    public void destroy() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        context = null;
        ProgressDialogUtil.destroy();
        helper = null;
    }

    public static class ProgressDialogUtil {
        private static ProgressDialog pd;

        public static void show(Context context) {
            // 创建进度对话框
            pd = new ProgressDialog(context);
            pd.setMax(100);
            // 设置对话框标题
            pd.setTitle("下载更新");
            // 设置对话框显示的内容
            pd.setMessage("进度...");
            // 设置对话框不能用取"消按"钮关闭
            pd.setCancelable(false);
            // 设置对话框的进度条风格
            // pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 设置对话框的进度条是否显示进度
            pd.setIndeterminate(false);
            pd.show();
        }

        public static void setProgress(int progress) {
            if (pd == null) {
                return;
            }
            pd.setProgress(progress);
        }

        public static void dismiss() {
            pd.dismiss();
            pd = null;
        }

        public static void destroy() {
            pd = null;
        }
    }

}
