/*
 * Copyright (C) 2018-2019 Gigadrive - All rights reserved.
 * https://gigadrivegroup.com
 * https://qpo.st
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://gnu.org/licenses/>
 */

package st.qpo.android;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;
import java.util.regex.Pattern;

public class QWebViewClient extends WebViewClient {
    private Pattern scopePattern;

    public QWebViewClient(String startURL, String scope) {
        try {
            URL baseURL = new URL(startURL);
            URL scopeURL = new URL(baseURL, scope);

            if (!scopeURL.toString().endsWith("*")) {
                scopeURL = new URL(scopeURL, "*");
            }

            this.scopePattern = this.regexFromPattern(scopeURL.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (this.scoped(url)) {
            return false;
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(i);
            return true;
        }
    }

    private boolean scoped(String url) {
        return this.scopePattern == null || this.scopePattern.matcher(url).matches();
    }

    private Pattern regexFromPattern(String pattern) {
        final String toReplace = "\\.[]{}()^$?+|";
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '*') {
                regex.append(".");
            } else if (toReplace.indexOf(c) > -1) {
                regex.append('\\');
            }
            regex.append(c);
        }
        return Pattern.compile(regex.toString());
    }
}
