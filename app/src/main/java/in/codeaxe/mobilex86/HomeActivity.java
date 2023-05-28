package in.codeaxe.mobilex86;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    TextView deviceInfo;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
        progressDialog.setMessage("Data Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                TextView deviceInfo = findViewById(R.id.deviceInfo);
                deviceInfo.setVisibility(View.VISIBLE);
                deviceInfo.setText(getDetails());
            }
        }, 4000);
    }
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        try {

            details.append("OS version: ").append(System.getProperty("os.version")).append("\n");
            details.append("API Level: ").append(android.os.Build.VERSION.SDK_INT).append("\n");
            details.append("Device: ").append(android.os.Build.DEVICE).append("\n");
            details.append("Model: ").append(android.os.Build.MODEL).append("\n");
            details.append("Product: ").append(android.os.Build.PRODUCT).append("\n");
            details.append("VERSION.RELEASE: ").append(Build.VERSION.RELEASE).append("\n");
            details.append("VERSION.INCREMENTAL: ").append(Build.VERSION.INCREMENTAL).append("\n");
            details.append("VERSION.SDK.NUMBER: ").append(Build.VERSION.SDK_INT).append("\n");
            details.append("BOARD: ").append(Build.BOARD).append("\n");
            details.append("BOOTLOADER: ").append(Build.BOOTLOADER).append("\n");
            details.append("BRAND: ").append(Build.BRAND).append("\n");
            details.append("CPU_ABI: ").append(Build.CPU_ABI).append("\n");
            details.append("CPU_ABI2: ").append(Build.CPU_ABI2).append("\n");
            details.append("DISPLAY: ").append(Build.DISPLAY).append("\n");
            details.append("FINGERPRINT: ").append(Build.FINGERPRINT).append("\n");
            details.append("HARDWARE: ").append(Build.HARDWARE).append("\n");
            details.append("HOST: ").append(Build.HOST).append("\n");
            details.append("ID: ").append(Build.ID).append("\n");
            details.append("MANUFACTURER: ").append(Build.MANUFACTURER).append("\n");
            details.append("MODEL: ").append(Build.MODEL).append("\n");
            details.append("PRODUCT: ").append(Build.PRODUCT).append("\n");
            details.append("SERIAL: ").append(Build.SERIAL).append("\n");
            details.append("TAGS: ").append(Build.TAGS).append("\n");
            details.append("TIME: ").append(Build.TIME).append("\n");
            details.append("TYPE: ").append(Build.TYPE).append("\n");
            details.append("UNKNOWN: ").append(Build.UNKNOWN).append("\n");
            details.append("USER: ").append(Build.USER).append("\n");


            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            details.append("ScreenWidth: ").append(width).append("\n");
            details.append("ScreenHeight: ").append(height).append("\n");
            details.append("Density: ").append(metrics.density).append("\n");
            details.append("DensityDpi: ").append((int) (metrics.density * 160f)).append("\n");

            // Battery information
            IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryIntent = registerReceiver(null, batteryIntentFilter);
            if (batteryIntent != null) {
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int batteryLevel = (int) ((level / (float) scale) * 100);

                int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String batteryStatus;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    batteryStatus = "Charging";
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING ||
                        status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    batteryStatus = "Discharging";
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    batteryStatus = "Full";
                } else {
                    batteryStatus = "Unknown";
                }

                details.append("BATTERY LEVEL: ").append(batteryLevel).append("%\n");
                details.append("BATTERY STATUS: ").append(batteryStatus).append("\n");
            }

            // CPU information
            String cpuInfo = readCPUInfo();
            details.append("CPU INFO: ").append(cpuInfo).append("\n");

            // GPU information
            String gpuInfo = readGPUInfo();
            details.append("GPU INFO: ").append(gpuInfo).append("\n");

            // JVM information
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long usedMemory = totalMemory - freeMemory;

            details.append("MAX MEMORY: ").append(maxMemory / (1024 * 1024)).append(" MB\n");
            details.append("TOTAL MEMORY: ").append(totalMemory / (1024 * 1024)).append(" MB\n");
            details.append("FREE MEMORY: ").append(freeMemory / (1024 * 1024)).append(" MB\n");
            details.append("USED MEMORY: ").append(usedMemory / (1024 * 1024)).append(" MB\n");

            // RAM information
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                long totalRam = memoryInfo.totalMem;
                long availableRam = memoryInfo.availMem;
                long usedRam = totalRam - availableRam;

                details.append("RAM TOTAL: ").append(totalRam / (1024 * 1024)).append(" MB\n");
                details.append("RAM AVAILABLE: ").append(availableRam / (1024 * 1024)).append(" MB\n");
                details.append("RAM USED: ").append(usedRam / (1024 * 1024)).append(" MB\n");
            }


            // MAC Address
            String macAddress = getMacAddress();
            details.append("MAC ADDRESS: ").append(macAddress).append("\n");

            // Append IP address
            String ipAddress = getIPAddress();
            details.append("IP ADDRESS: ").append(ipAddress).append("\n");

            // Append current location (if available)
            String location = getCurrentLocation();
            if (location != null) {
                details.append("LOCATION: ").append(location).append("\n");
            }

            // Check if the device is rooted
            boolean isRooted = isRootedDevice();
            details.append("ROOTED: ").append(isRooted ? "Yes" : "No").append("\n");


            // SIM Card details
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                String simOperatorName = telephonyManager.getSimOperatorName();
                String simSerialNumber = telephonyManager.getSimSerialNumber();
                details.append("SIM OPERATOR NAME: ").append(simOperatorName).append("\n");
                details.append("SIM SERIAL NUMBER: ").append(simSerialNumber).append("\n");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return details.toString();
    }


    /*Ip Address*/
    private String getIPAddress() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // Get Wi-Fi IP address
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager != null) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        int ipAddress = wifiInfo.getIpAddress();
                        return Formatter.formatIpAddress(ipAddress);
                    }
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // Get mobile data IP address
                    try {
                        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                            NetworkInterface networkInterface = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    //   Get MacAddress
    private String getMacAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = networkInterface.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder macAddress = new StringBuilder();
                        for (byte b : macBytes) {
                            macAddress.append(String.format("%02X:", b));
                        }
                        if (macAddress.length() > 0) {
                            macAddress.deleteCharAt(macAddress.length() - 1);
                        }
                        return macAddress.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    //    rooted Device
    private boolean isRootedDevice() {
        // Check for common indicators of rooted devices
        String[] rootIndicators = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su",
                "/system/app/Superuser.apk",
                "/system/app/SuperSU.apk",
                "/system/app/KingRoot.apk",
                "/system/app/MagiskManager.apk"
        };

        for (String indicator : rootIndicators) {
            if (new File(indicator).exists()) {
                return true;
            }
        }

        // Check if the device has a rooted build tags
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // Check for the presence of the "su" executable
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            return line != null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return false;
    }
    private String readCPUInfo() {
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private String readGPUInfo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
            if (configInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
                return "OpenGL ES Version: " + Integer.toHexString(configInfo.reqGlEsVersion);
            }
        }
        return "N/A";
    }
    private String getCurrentLocation() {
        // Implement your own logic to retrieve the current location.
        // You can use location APIs like Google Play Services or Android LocationManager to get the location coordinates.
        // This code snippet assumes you have a method that returns the location as a string.
        // Replace this code with your implementation.

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            requestPermissions(new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.BATTERY_STATS,
                    android.Manifest.permission.INTERNET
            }, 1);

        } else {
            // Location permissions granted, retrieve current location
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        // Handle location change if needed
                        // This method will be called when the location is updated
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // Handle status change if needed
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // Handle provider enabled if needed
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // Handle provider disabled if needed
                    }
                }, null);
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Convert latitude and longitude to address or retrieve other location details
                    // and return it as a string
                    return "Latitude: " + latitude + ", Longitude: " + longitude;
                }
            }
        }
        return null;
    }
}