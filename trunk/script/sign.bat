cd %~dp0
jarsigner.exe -verbose -keystore com.fuyong_android.keystore -storepass fuyong -keypass fuyong -signedjar fuyong_signed.apk fuyong.apk com.fuyong_android.keystore