package game;



import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ErsBlock extends Thread{
    public final static int BOXES_ROWS=4;
    public final static int BOXES_CLOS=4;

    public final static int LEVEL_FLATNESS_GENE=3;

    public final static int BETWEEN_LEVELS_DEGRESS_TIME=50;

    private final static int BLOCK_KIND_NUMBER=7;

    private final static int BLOCK_STATUS_NUMBER=4;

    public final static int[][] STYLES= {
            {0x0f00, 0x4444, 0x0f00, 0x4444},
            {0x04e0, 0x0464, 0x00e4, 0x6c00},
            {0x4260, 0x6c00, 0x4620, 0x6c00},
            {0x2640, 0xc600, 0x2640, 0xc600},
            {0x6220, 0x1700, 0x2230, 0x0740},
            {0x6440, 0x0e20, 0x44c0, 0x8e00},
            {0x0660, 0x0660, 0x0660, 0x0660},
    };
    private  GameCanvas canvas;
    private ControlPanel panel;
    private ErsBox[][]boxes=new ErsBox[BOXES_ROWS][BOXES_CLOS];
    private int style,y,x,level;
    private boolean moving=true;
    private int[][]canvasData;
    private int[][]recData;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int numLose;
    private boolean fullLine=false;

    public ErsBlock(int style,int y,int x,int level,GameCanvas canvas,
                    ControlPanel panel,ObjectInputStream in,
                    ObjectOutputStream out,int numLose){
        this.style=style;
        this.y=y;
        this.x=x;
        this.level=level;
        this.canvas=canvas;
        this.panel=panel;
        this.in=in;
        this.out=out;
        this.numLose=numLose;

        int key=0x8000;
        for (int i=0;i<boxes.length;i++){
            for (int j=0;j<boxes[i].length;j++){
                boolean isColor=((style&key)!=0);
                boxes[i][j]=new ErsBox(isColor);
                key>>=1;
            }
        }
        display();
    }

    public void run(){
        Data data=null;

    }

    public void moveLeft(){
        moveTo(y,x-1);
    }
    public void moveRight(){
        moveTo(y,x+1);
    }
    public void moveDown(){
        moveTo(y+1,x);
    }


    public void turnNext(){
        for (int i=0;i<BLOCK_KIND_NUMBER;i++){
            for (int j=0;j<BLOCK_STATUS_NUMBER;j++){
                if (STYLES[i][j]==style){
                    int newStyle=STYLES[i][(j+1)%BLOCK_STATUS_NUMBER];
                    turnTo(newStyle);
                    return;
                }
            }
        }
    }

    private void earse(){
        for (int i=0;i<boxes.length;i++){
            for (int j=0;j<boxes[i].length;j++){
                if (boxes[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(i+y,j+x);
                    if (box==null)
                        continue;
                    box.setColor(false);
                }
            }
        }
    }

    private void display(){
        for (int i=0;i<boxes.length;i++){
            for (int j=0;j<boxes[i].length;j++){
                if (boxes[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(y+i,x+j);
                    if (box==null)
                        continue;
                    box.setColor(true);
                }
            }
        }
    }

    private boolean isMoveAble(int newRow,int newCol){
        earse();
        for (int i=0;i<boxes.length;i++){
            for (int j=0;j<boxes[i].length;j++){
                if (boxes[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(newRow+i,newCol+j);
                    if (box==null||(box.isColorBox())){
                        display();
                        return false;
                    }
                }
            }
        }
        display();
        return true;

    }

    private synchronized boolean moveTo(int newRow,int newCol){
        if (!isMoveAble(newRow,newCol)||!moving)
            return false;
        earse();
        y=newRow;
        x=newCol;
        display();
        canvas.repaint();
        return true;
    }


    private boolean isTurnAble(int newStyle){
        int key=0x8000;
        earse();
        for (int i=0;i<boxes.length;i++){
            for (int j=0;j<boxes[i].length;j++){
                if ((newStyle&key)!=0){
                    ErsBox box=canvas.getBox(y+i,x+j);
                    if (box==null||box.isColorBox()){
                        display();
                        return false;
                    }
                }
                key >>=1;
            }
        }
        display();
        return true;
    }




    private boolean turnTo(int newStyle){
        if (!isTurnAble(newStyle)||!moving)
            return false;
        earse();
        int key=0x8000;
        for (int i=0;i<boxes.length;i++){
            for (int j=0;j<boxes[i].length;j++){
                boolean isColor=((newStyle&key)!=0);
                boxes[i][j].setColor(isColor);
                key>>=1;

            }
        }
        style=newStyle;
        display();
        canvas.repaint();
        return true;

    }



}
