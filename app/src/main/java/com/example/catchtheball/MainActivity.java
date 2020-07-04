package com.example.catchtheball;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //Frame
    private FrameLayout manhinhgame;
    private int frameWidth,frameHeight,initialFrameWidht;
    private LinearLayout manhinh;
    //Diem cao
    private SharedPreferences luudiemso;
    //Image
    private ImageView pac,bom,cam;
    private Drawable pacman1,Pacman2;
    //Size
    private int pacsize;
    //Position
    private float pacX,pacY;
    private float bomX, bomY;
    private float camX,camY;
    //Điểm
    private TextView diem,highscore;
    private int score,high, timecount;
    // Class
    private Timer timer;
    private Handler handler = new Handler();
    //Status
    private boolean start_flg = false;
    private boolean action_flg= false;
    private boolean pause_flg= false;
    //Button
    private Button pauseBt;
    private Button ExitBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Anh xa
        manhinhgame = findViewById(R.id.manhinh_game);
        manhinh = findViewById(R.id.manhinh);
        pac = findViewById(R.id.pacRight);
        bom= findViewById(R.id.boom);
        cam = findViewById(R.id.Orange);
        diem = findViewById(R.id.diem);
        highscore= findViewById(R.id.highscore);

        ExitBt = findViewById(R.id.Exit);
        pauseBt = findViewById(R.id.pausebt);

// Lay hinh pacman2, pacman1
        Pacman2 = getResources().getDrawable(R.drawable.pacman2);
        pacman1 = getResources().getDrawable(R.drawable.pacman1);

        //highscore
        luudiemso = getSharedPreferences("du_lieu",Context.MODE_PRIVATE);
        high=luudiemso.getInt("diemcao",0);
        highscore.setText("High Score: "+high);


    }
    public void changePos(){
        //cam
        camY +=12;
        // vi tri giua truc x va truc y
        float camcenterx= camX+cam.getWidth()/2;
        float camcentery= camY+cam.getHeight()/2;
        // vi tri chạm của pac vào cam
        if(hitCheck(camcenterx,camcentery))
        {
            camY = frameHeight+100;
            score+=10;
        }
        if(camY>frameHeight){
            camY = -100;
            //random vị trí rơi của cam
            camX=(float)Math.floor(Math.random()*(frameWidth-cam.getWidth()));
        }
        //set tọa độ cho cam
        cam.setX(camX);
        cam.setY(camY);
        //bom
        bomY+=18;
        float bomcenterX = bomX+bom.getWidth()/2;
        float bomcenterY= bomY+bom.getHeight()/2;
        if(hitCheck(bomcenterX,bomcenterY))
        {
            bomY= frameHeight+100;
            //Thay doi man hinh choi game
            frameWidth=frameWidth*80/100;
            changeFrameWidht(frameWidth);
            // DK Game over
            if(frameWidth<pacsize)
            {
                gameOver();
            }

        }
        if(bomY>frameHeight){
            bomY = -100;
            bomX=(float)Math.floor(Math.random()*(frameWidth-bom.getWidth()));
        }
        bom.setX(bomX);
        bom.setY(bomY);



        //Di chuyen
        if(action_flg)
        {
            pacX+=14;
            pac.setImageDrawable(pacman1);
        }else {
            pacX -= 14;
            pac.setImageDrawable(Pacman2);
        }
        //kiem tra vi tri
        if (pacX<0)
        {
            pacX=0;
            pac.setImageDrawable(pacman1);
        }
        if(frameWidth-pacsize<pacX)
        {
            pacX= frameWidth-pacsize;
            pac.setImageDrawable(Pacman2);

        }
        pac.setX(pacX);

        diem.setText("Score: "+score);
    }

    private void gameOver() {
        timer.cancel();
        timer= null;
        start_flg=false;
        //Truoc khi hien man hinh , dung 1s
        try{
            TimeUnit.SECONDS.sleep(1);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        changeFrameWidht(initialFrameWidht);
        manhinh.setVisibility(View.VISIBLE);
        pac.setVisibility(View.INVISIBLE);
        cam.setVisibility(View.INVISIBLE);
        bom.setVisibility(View.INVISIBLE);

        //Update diem cao
        if(score>high)
        {
            high=score;
            highscore.setText("High Score: "+high);
            SharedPreferences.Editor editor = luudiemso.edit();
            editor.putInt("diemcao",high);
            editor.commit();

        }

    }

    private void changeFrameWidht(int frameWidth) {
        // đặt các thuộc tính vào LayoutParams
        ViewGroup.LayoutParams params= manhinhgame.getLayoutParams();
        params.width=frameWidth;
        manhinhgame.setLayoutParams(params);
    }
    private boolean hitCheck(float x, float y) {
        if(pacX <= x && x<=pacX+pacsize && pacY<=y && y<=frameHeight)
        {
            return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(start_flg){
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                action_flg=true;
            }else if(event.getAction()==MotionEvent.ACTION_UP)
            {
                action_flg=false;
            }
        }
        return true;
    }
    public void startGame(View view)
    {
        start_flg = true;
        manhinh.setVisibility(View.INVISIBLE);
        ExitBt.setVisibility(View.VISIBLE);
        pauseBt.setVisibility(View.VISIBLE);
        if(frameHeight==0)
        {
            frameHeight= manhinhgame.getHeight();
            frameWidth= manhinhgame.getWidth();
            initialFrameWidht = frameWidth;
            pacsize = pac.getHeight();
            pacX= pac.getX();
            pacY= pac.getY();

        }
        frameWidth= initialFrameWidht;
        pac.setX(0.0f);
        bom.setY(3000.0f);
        cam.setY(3000.0f);
        bomY = bom.getY();
        camY = cam.getY();

        pac.setVisibility(View.VISIBLE);
        bom.setVisibility(View.VISIBLE);
        cam.setVisibility(View.VISIBLE);


        timecount=0;
        score =0;
        diem.setText("Score : 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(start_flg){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }

            }
        }, 0,20);
    }
    public void Pause(View view)
    {
        if(pause_flg== false)
        {
            pause_flg=true;
            timer.cancel();
            timer=null;

            pauseBt.setText("Start");
        }
        else
        {
            pause_flg=false;
            pauseBt.setText("Pause");
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(start_flg){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePos();
                            }
                        });
                    }

                }
            }, 0,20);
        }
    }
    public void QuitGame(View view)
    {
        // Kiểm tra xem bạn chạy android 5.0 hay cao hơn
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            // thoát ứng dụng
            finishAndRemoveTask();
        }else {
            finish();
        }

    }

}
