package com.lzy.smallseal;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<String> chars = new ArrayList<>();
    private int index = 0;
    private boolean seal = true;

    private Typeface sealFont;
    private Typeface normalFont;

    private ImageView imageView;
    private GestureDetector gestureDetector;
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        setContentView(imageView);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        sealFont = Typeface.createFromAsset(getAssets(), "FZXZTFW.TTF");
        normalFont = Typeface.createFromAsset(getAssets(), "FZXKTK.TTF");

        try {
            InputStream is = getAssets().open("simp.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                for (char c : line.toCharArray()) {
                    chars.add(String.valueOf(c));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.shuffle(chars);

        gestureDetector = new GestureDetector(this, new GestureListener());

        show();
    }

    private void show() {
        String text = chars.get(index);

        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTypeface(seal ? sealFont : normalFont);
        paint.setTextSize(seal ? 350 : 300);

        float textWidth = paint.measureText(text);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;

        float x = (bitmap.getWidth() - textWidth) / 2;
        float y = (bitmap.getHeight() - textHeight) / 2 - fm.top;

        canvas.drawText(text, x, y, paint);

        imageView.setImageBitmap(bitmap);
    }

    private void slide(boolean left) {
        if (left) {
            index--;
            if (index < 0) index = chars.size() - 1;
        } else {
            index++;
            if (index >= chars.size()) index = 0;
        }
        seal = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            seal = !seal;
            show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            String text = chars.get(index);
            ClipData clip = ClipData.newPlainText("char", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            slide(!(velocityX > 0));
            show();
            return true;
        }
    }
}
