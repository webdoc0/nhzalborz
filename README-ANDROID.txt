اپ اندروید (WebView) — نهضت البرز
آدرس: https://nhzalborz.ir/index-app.php

ساخت APK:
1) Android Studio را باز کنید → Open → همین پوشه (صبر کنید Gradle Sync تمام شود).
2) تست: Run روی گوشی یا شبیه‌ساز.
3) APK: Build → Build APK(s) ؛ برای انتشار در مارکت‌ها: Build → Generate Signed Bundle / APK.

امکانات: کوکی ماندگار (لاگین باز می‌ماند)، DOM Storage، آپلود فایل/عکس، دکمه‌ی بازگشت،
صفحه‌ی آفلاین (assets/offline.html)، باز شدن لینک‌های بیرونی در برنامه‌ی مناسب.
برای ماندن لاگین، سمت سرور هم pwa-session.php (ارسال قبلی) را فعال کنید.

ساخت APK بدون Android Studio (GitHub Actions):
1) در github.com یک مخزن (repository) جدید بسازید.
2) محتویات همین پوشه (نه خود پوشه؛ همراه پوشه‌ی مخفی .github) را در ریشه‌ی مخزن آپلود کنید.
3) تب Actions → Build APK → Run workflow. بعد از چند دقیقه، فایل nhz-alborz-apk را از پایین صفحه‌ی اجرا دانلود کنید.
4) داخل زیپ، app-debug.apk است؛ روی گوشی نصب کنید (نصب از منبع ناشناس را مجاز کنید).
