package com.example.songchiyun.mazegame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by songchiyun on 16. 5. 27..
 */
public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {

    boolean correctMaze = false;
    Canvas cacheCanvas;
    Bitmap backBuffer;
    int width = 16, height = 24, clientHeight;
    int desX, desY;
    int w, h;
    Paint paint;
    Context context;
    SurfaceHolder mHolder;
    int userX, userY;
    boolean[][] block = new boolean[22][14];   // block
    boolean[][] used = new boolean[22][14];  //to use random map
    boolean[][] road = new boolean[22][14]; //to use random map

    Bitmap user;


    public DrawingSurface(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    private void init() {

        mHolder = getHolder();
        mHolder.addCallback(this);
        w = 14;
        h = 22;
        Bitmap pic = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        userX = 25; // first position where character is located
        userY = 25;
        user = Bitmap.createScaledBitmap(pic, 50, 50, false);

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {

        paint = new Paint();

        width = getWidth();
        height = getHeight();
        cacheCanvas = new Canvas();
        backBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // back buffer
        cacheCanvas.setBitmap(backBuffer);
        cacheCanvas.drawColor(Color.WHITE);



        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);


        draw();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    int lastX, lastY, currX, currY;
    boolean isDeleting;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();

        int testX, testY;
        testX = (int)event.getX();
        testY = (int)event.getY();

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                Log.d("check", String.valueOf(userX)+"/"+String.valueOf(userY));
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                if((lastX-(userX))*(lastX-(userX)) + (lastY-(userY))*(lastY-(userY)) > 20*20){
                    Log.d("check", String.valueOf(lastX)+"/"+String.valueOf(lastY));
                    isDeleting = true;
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDeleting) break;
                if(testX  <= 0 || testX >= 700 || testY <= 0 || testY >= 1100){
                    reset();
                }
                else {
                    for (int i = 0; i < h; i++) {
                        for (int j = 0; j < w; j++) {
                            if (!block[i][j]) {
                                if (testX >= j * 50 && testX <= j * 50 + 50 && testY >= i * 50 && testY <= i * 50 + 50) {
                                    reset();
                                }

                            }
                        }
                    }
                }
                currX = (int) event.getX();
                currY = (int) event.getY();
                cacheCanvas.drawLine(lastX, lastY, currX, currY, paint);
                lastX = currX;
                lastY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (isDeleting) isDeleting = false;
                else{
                    userX = lastX;
                    userY = lastY;
                    isDeleting = true;
                    cacheCanvas.drawColor(Color.BLACK);
                    settingMaze();
                    move();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
              //  cacheCanvas.drawColor(Color.WHITE);
               isDeleting = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        draw(); // SurfaceView에 그리는 function을 직접 제작 및 호출
        return true;
    }
    private void move(){

        cacheCanvas.drawBitmap(user,userX-25,userY-25,null);
    }
    private void reset(){
        isDeleting = true;
        userX = 25;
        userY = 25;
        settingMaze();
        cacheCanvas.drawBitmap(user,userX-25,userY-25,null);
    }
    private void makingRoad(){
        int rand;
        for(int i = 0; i < 22; i++) {
            for(int j = 0 ; j < 14; j++) {
                if(road[i][j]){
                    block[i][j] = true;
                    continue;
                }
                else {
                    rand = new Random().nextInt(4);
                    if(rand == 0)
                         block[i][j] = true;
                    else
                        block[i][j] = false;
                }
            }
        }
    }
    private void settingMaze(){   //drawing maze through block is true or false
        Paint r = new Paint();
        Paint b = new Paint();
        Paint start = new Paint();
        Paint finish = new Paint();
        b.setColor(Color.BLACK);  // block color
        r.setColor(Color.WHITE);  //road color
        start.setColor(Color.RED);
        finish.setColor(Color.GREEN);

        cacheCanvas.drawColor(Color.BLACK);
        int rand;
        for(int i = 0; i < 22; i++) {
            for(int j = 0 ; j < 14; j++) {
                if(block[i][j]){
                    cacheCanvas.drawRect(j * 50, i * 50, j * 50 + 50, i * 50 + 50, r);
                }
                else {
                    cacheCanvas.drawRect(j * 50, i * 50, j * 50 + 50, i * 50 + 50, b);
                }
            }
        }

        //setting other color in start point and finish point
        cacheCanvas.drawRect(0,0,50,50,start);
        cacheCanvas.drawRect(desX*50, desY*50, desX*50+50, desY*50+50, finish);

    }

    private void generateMaze() {   // to make random maze, then each partition get block or road randomly
        int ran;
        block[0][0] = true;  //start point
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 14; j++) {
                if(block[i][j])
                    continue;

                ran = new Random().nextInt(3);

                if (ran == 0)   //road
                    block[i][j] = false;
                else
                    block[i][j] = true;

            }
        }

    }


    protected void draw() {
        if (clientHeight == 0) {
            clientHeight = getClientHeight();
            height = clientHeight;
            backBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            cacheCanvas.setBitmap(backBuffer);
            cacheCanvas.drawColor(Color.WHITE);
         //   solveMaze();
            while(true) {  //making maze randomly when this app is created
                generateMaze();    //first setting each partition randomly
                if(checkMaze(0, 0)){  //check maze to use recursive, if true then draw maze , else resetting maze
                    makingRoad();
                    settingMaze();
                    break;
                }
                refresh();
                Log.d("check:","fals rerun");

            }
            cacheCanvas.drawBitmap(user,userX-25,userY-25,null);

        }
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas(null);
//back buffer에 그려진 비트맵을 스크린 버퍼에 그린다
          canvas.drawBitmap(backBuffer, 0,0, paint);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (mHolder != null) mHolder.unlockCanvasAndPost(canvas);
        }
    }
    private void refresh(){   //refres is used to make random maze (used is trace so, sweep trace when reset maze)
        for(int i = 0; i < 22; i++)
            for(int j = 0; j < 14; j++)
                used[i][j] = false;
    }
    //22 14
    private boolean checkMaze(int x, int y){  //checking maze whether or not to go next partition
        used[y][x] = true;

        if(y == 21){  //if last position then stop to recursive
            desX = x;
            desY = y;
            road[y][x] = true;
            Log.d("check:","finish");
            correctMaze = true;
            return true;
        }
        if(x > 0 && block[y][x-1] && !used[y][x-1]){
            Log.d("check:","first");
            if(checkMaze(x-1, y)) {
                road[y][x] = true;
                return true;
            }
        }
        if(x < 13 && block[y][x+1] && !used[y][x+1]){
            Log.d("check:","first2");
            if(checkMaze(x+1, y)) {
                road[y][x] = true;
                return true;
            }
        }
        if(y > 0 && block[y-1][x] && !used[y-1][x]){
            Log.d("check:","first3");
            if(checkMaze(x, y-1)) {
                road[y][x] = true;
                return true;
            }
        }
        if(y < 21 && block[y+1][x] && !used[y+1][x]){
            Log.d("check:","first4");
            if(checkMaze(x, y+1)){
                road[y][x] = true;
                return true;
            }


        }
        Log.d("r","return false"+x+"/"+y);
        return false;
    }
    /* 상태바, 타이틀바를 제외한 클라이언트 영역의 높이를 구한다 */
    private int getClientHeight() {
        Rect rect = new Rect();
        Window window = ((Activity) context).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        return ((Activity) context).getWindowManager().getDefaultDisplay().
                getHeight() - statusBarHeight - titleBarHeight;
    }
}
