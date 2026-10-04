package com.storylingo.app;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Locale;

public class MainActivity extends Activity implements OnInitListener {

    private WebView webView;
    private TextToSpeech tts;
    private MediaPlayer mediaPlayer;
    private boolean ttsReady = false;
    private static final String ONLINE_URL = "https://ahmadhibban.github.io/StoryLingo/";
    private static final String OFFLINE_URL = "file:///android_asset/index.html";

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnected();
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        // Initialize Android Text-To-Speech Engine
        tts = new TextToSpeech(this, this);

        webView = findViewById(R.id.webview1);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setUserAgentString(ws.getUserAgentString() + " StoryLingoApp/1.0");

        // Android Native TTS & Audio Bridge
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (failingUrl != null && failingUrl.startsWith("http")) {
                    view.loadUrl(OFFLINE_URL);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request != null && request.isForMainFrame() && request.getUrl() != null && request.getUrl().toString().startsWith("http")) {
                    view.loadUrl(OFFLINE_URL);
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        if (isNetworkAvailable()) {
            webView.loadUrl(ONLINE_URL);
        } else {
            webView.loadUrl(OFFLINE_URL);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                ttsReady = true;
            }
            
            // Young boy voice configuration (pitch 0.85f, natural storytelling speed 0.92f)
            tts.setPitch(0.85f);
            tts.setSpeechRate(0.92f);

            // Attempt to select an English male / boy voice if available
            try {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    for (android.speech.tts.Voice voice : tts.getVoices()) {
                        if (voice.getLocale().getLanguage().startsWith("en")) {
                            String n = voice.getName().toLowerCase();
                            if ((n.contains("male") || n.contains("boy") || n.contains("rjs")) && !n.contains("female")) {
                                tts.setVoice(voice);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (webView != null) {
                                webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechStart) { window.app.onNativeSpeechStart(0); }", null);
                            }
                        }
                    });
                }

                @Override
                public void onDone(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (webView != null) {
                                webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechEnd) { window.app.onNativeSpeechEnd(); }", null);
                            }
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (webView != null) {
                                webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechEnd) { window.app.onNativeSpeechEnd(); }", null);
                            }
                        }
                    });
                }

                @Override
                public void onRangeStart(String utteranceId, final int start, final int end, int frame) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (webView != null) {
                                webView.evaluateJavascript("if (window.app && window.app.onNativeWordRange) { window.app.onNativeWordRange(" + start + ", " + end + "); }", null);
                            }
                        }
                    });
                }
            });
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public boolean isTTSAvailable() {
            return ttsReady;
        }

        @JavascriptInterface
        public void speak(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Route to playBoyVoice for guaranteed teenage boy voice
                    playBoyVoiceInternal(text);
                }
            });
        }

        @JavascriptInterface
        public void playBoyVoice(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playBoyVoiceInternal(text);
                }
            });
        }

        @JavascriptInterface
        public void playAudioUrl(final String url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playUrlInternal(url);
                }
            });
        }

        @JavascriptInterface
        public void stopAudio() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAudioInternal();
                }
            });
        }

        @JavascriptInterface
        public void closeApp() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

    private void playBoyVoiceInternal(final String text) {
        if (text == null || text.trim().isEmpty()) return;
        try {
            String encoded = java.net.URLEncoder.encode(text.trim(), "UTF-8");
            String url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=en&client=tw-ob&q=" + encoded;
            playUrlInternal(url);
        } catch (Exception e) {
            e.printStackTrace();
            if (tts != null && ttsReady) {
                tts.setPitch(0.85f);
                tts.setSpeechRate(0.92f);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "storylingo_tts");
            }
        }
    }

    private void playUrlInternal(final String url) {
        try {
            stopAudioInternal();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        mp.start();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            try {
                                android.media.PlaybackParams params = mp.getPlaybackParams();
                                if (params == null) {
                                    params = new android.media.PlaybackParams();
                                }
                                params.setPitch(0.85f);
                                params.setSpeed(0.92f);
                                mp.setPlaybackParams(params);
                            } catch (Exception ex) {
                                try {
                                    android.media.PlaybackParams params = new android.media.PlaybackParams();
                                    params.setPitch(0.85f);
                                    params.setSpeed(0.92f);
                                    mp.setPlaybackParams(params);
                                } catch (Exception ignored) {}
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int rawDuration = mp.getDuration();
                    final int actualDurationMs = (rawDuration > 0) ? (int)(rawDuration / 0.92f) : 0;
                    if (webView != null) {
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechStart) { window.app.onNativeSpeechStart(" + actualDurationMs + "); }", null);
                            }
                        });
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (webView != null) {
                        webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechEnd) { window.app.onNativeSpeechEnd(); }", null);
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (webView != null) {
                        webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechEnd) { window.app.onNativeSpeechEnd(); }", null);
                    }
                    return true;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            if (webView != null) {
                webView.evaluateJavascript("if (window.app && window.app.onNativeSpeechEnd) { window.app.onNativeSpeechEnd(); }", null);
            }
        }
    }

    private void stopAudioInternal() {
        if (tts != null) {
            tts.stop();
        }
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            // Give Web app the first opportunity to handle the back button (e.g. exit reader to library)
            webView.evaluateJavascript("if (window.app && window.app.handleBackButton) { window.app.handleBackButton(); } else { window.history.back(); }", null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAudioInternal();
        if (webView != null) {
            webView.evaluateJavascript("if (window.app && window.app.stopAudio) { window.app.stopAudio(); }", null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAudioInternal();
        if (webView != null) {
            webView.evaluateJavascript("if (window.app && window.app.stopAudio) { window.app.stopAudio(); }", null);
        }
    }

    @Override
    protected void onDestroy() {
        stopAudioInternal();
        if (tts != null) {
            tts.shutdown();
        }
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
