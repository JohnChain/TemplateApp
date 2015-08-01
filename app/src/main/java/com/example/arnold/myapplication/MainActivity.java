package com.example.arnold.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public Button changeB;
    public Button addB;
    public Button saveB;
    public Button calcuB;
    public TableLayout parentTable;
    public TextView totalTV;

    private DBHelper DBHP;

    public boolean changing = false;
    public static final String MYTAG = "johnchain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.changeB = new Button(this);
        this.addB = new Button(this);
        this.saveB = new Button(this);
        this.parentTable = new TableLayout(this);
        this.totalTV = new TextView(this);

        changeB = (Button)findViewById(R.id.changeB);
        addB = (Button)findViewById(R.id.addB);
        saveB = (Button)findViewById(R.id.saveB);
        calcuB = (Button)findViewById(R.id.calcuB);
        parentTable = (TableLayout)findViewById(R.id.parentTable);
        totalTV = (TextView)findViewById(R.id.totalTV);

        addB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddItem();
            }
        });
        changeB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onChangeItem();
            }
        });
        saveB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveItem();
            }
        });
        calcuB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCalculate();
            }
        });

        // TODO:
        // 开启AP时，查询数据库相应表内内容，并画表
        // /
        DBHelper.exist(DBHelper.DB_NAME);
        this.DBHP = new DBHelper(this);
        Cursor cursor = this.DBHP.select_all();
        int count = cursor.getCount();
        for(int i = 0; i < count; i++){
            cursor.moveToPosition(i);
            // 一旦数据表结构发生改变，这里需要做相应修改
            String item = cursor.getString(1);
            float price = cursor.getFloat(2);
            Log.e(MYTAG, "In select row: " + i + ": item: " + item + " price: " + price);
            addItem(item, price);
        }
        cursor.close();
    }

    /**
     * 由求总按钮触发，计算出“单价”*“数量”
     * */
    private boolean onCalculate(){
        int total = 0;
        int count = parentTable.getChildCount();
        for(int i = 0; i < count;i++){
            TableRow subRow = (TableRow)parentTable.getChildAt(i);
            String item = ((EditText)subRow.getChildAt(2)).getText().toString();
            String priceString = ((EditText) subRow.getChildAt(4)).getText().toString();
            String numberS = ((EditText) subRow.getChildAt(6)).getText().toString();

            if(!priceString.equals("") && !item.equals("") && !numberS.equals("")) {
                total += Float.parseFloat(numberS) * Float.parseFloat(priceString);
            }else{
                Log.e(MYTAG, "item and price cannot be null");
                Toast toast = Toast.makeText(getApplicationContext(), "项目/单价/数量 都不能为空", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }
        Log.e(MYTAG, "total momey = " + total);
        this.totalTV.setText("" + total);
        return true;
    }

    /**
     * 由保存按钮触发， 将“项目”，“单价”列数据保存为模板，
     * 1：将所有可编辑项设为 disable状态
     * 2：将编辑项的值存储到数据库
     * */
    private boolean onSaveItem(){

        this.changing = false;
        this.DBHP.delete_all();

        int count = parentTable.getChildCount();
        for(int i = 0; i < count;i++){
            TableRow subRow = (TableRow)parentTable.getChildAt(i);
            String item = ((EditText)subRow.getChildAt(2)).getText().toString();
            String priceString = ((EditText) subRow.getChildAt(4)).getText().toString();
            String numberS = ((EditText) subRow.getChildAt(6)).getText().toString();
//            String item = ((EditText)subRow.findViewById(R.id.itemET1)).getText().toString();
//            String priceString = ((EditText) subRow.findViewById(R.id.priceET1)).getText().toString();

            if(!priceString.equals("") && !item.equals("")) {
                float price = Float.parseFloat(priceString);
                this.DBHP.insert(item, price);

                ((ImageButton)subRow.getChildAt(0)).setEnabled(false);
                ((EditText)subRow.getChildAt(2)).setEnabled(false);
                ((EditText)subRow.getChildAt(4)).setEnabled(false);
//                ((ImageButton)subRow.findViewById(R.id.deleteB1)).setEnabled(false);
//                ((EditText)subRow.findViewById(R.id.itemET1)).setEnabled(false);
//                ((EditText)subRow.findViewById(R.id.priceET1)).setEnabled(false);
            }else{
                Log.e(MYTAG, "item and price cannot be null");
                Toast toast = Toast.makeText(getApplicationContext(), "项目和单价不能为空", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }
        Cursor cursor = this.DBHP.select_all();
        for(int i = 0; i < count; i++){
            cursor.moveToPosition(i);
            // 一旦数据表结构发生改变，这里需要做相应修改
            String item = cursor.getString(1);
            float price = cursor.getFloat(2);
            Log.e(MYTAG, "In select row: " + i + ": item: " + item + " price: " + price);
        }
        cursor.close();
        return true;
    }

    /**
     * 删除一行
     * */
    public boolean onDeleteItem(TableRow subRow){
        // TODO:
        // 通过所点按钮找到其所在RowTable，然后删除该RowTable内所有控件及其本身
        //
        int index = parentTable.indexOfChild(subRow);
        parentTable.removeView(subRow);
        Log.e(MYTAG, "index = " + index + "removed");

//        // ADD: test MO, MT, SMS, PBR,
//        Toast toast= Toast.makeText(getApplicationContext(), "will delete SubRowTable ", Toast.LENGTH_SHORT);
//        toast.show();
//        String number = "10086";
//        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ number));
//        //startActivity(intent);
        return true;
    }

    /**
     * 修改以保存的数据，项目以及单价
     * */
    public boolean onChangeItem(){
        if(this.changing == true) {
            return true;
        }
        int total = 0;
        int count = parentTable.getChildCount();
        for(int i = 0; i < count;i++){
            TableRow subRow = (TableRow)parentTable.getChildAt(i);
            ((ImageButton)subRow.getChildAt(0)).setEnabled(true);
            ((EditText)subRow.getChildAt(2)).setEnabled(true);
            ((EditText)subRow.getChildAt(4)).setEnabled(true);
//            ((ImageButton)subRow.findViewById(R.id.deleteB1)).setEnabled(true);
//            ((EditText)subRow.findViewById(R.id.itemET1)).setEnabled(true);
//            ((EditText)subRow.findViewById(R.id.priceET1)).setEnabled(true);
        }
        this.changing = true;
        return true;
    }

    /**
     * 新增一行(由"新增”按钮触发）
     * */
    public boolean onAddItem(){
        createSubrow("", 0, true);
        return true;
    }
    /**
     * 新增一行
     * */
    public boolean addItem(String item, float price){
        createSubrow(item, price, false);
        return true;
    }

    private void createSubrow(String item, float price, boolean enable){
        ImageButton deleteB = new ImageButton(this);
//        deleteB.setId(R.id.deleteB1);
        deleteB.setAdjustViewBounds(true);
        deleteB.setBackgroundResource(R.drawable.dialog_close);
        deleteB.setPadding(0, 0, 0, 0);
        //delete.setEnabled(false);
        deleteB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(MYTAG, "Clicked Button parent type is: " + v.getParent().getClass().toString());
                onDeleteItem((TableRow) (v.getParent()));
            }
        });

        TextView itemTV = new TextView(this);
        itemTV.setText(R.string.item);

        EditText itemET = new EditText(this);
        itemET.setText(item);
        itemET.setEnabled(enable);

        TextView priceTV = new TextView(this);
        priceTV.setText(R.string.price);

        EditText priceET = new EditText(this);
        priceET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式
        priceET.setText("" + price);
        priceET.setEnabled(enable);

        TextView numberTV = new TextView(this);
        numberTV.setText(R.string.number);

        EditText numberET = new EditText(this);
        numberET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式
        numberET.setMinWidth(100);

        TableRow subTable = new TableRow(this);
        subTable.addView(deleteB);
        subTable.addView(itemTV);
        subTable.addView(itemET);
        subTable.addView(priceTV);
        subTable.addView(priceET);
        subTable.addView(numberTV);
        subTable.addView(numberET);
        parentTable.addView(subTable);
    }

//    // 获取与一个可用的index， 用于新建的RowTable
//    private int getIndex(){
//        int index = 0;
//        if(!reservedList.isEmpty()){
//            index = reservedList.remove(0);
//            indexList.add(index);
//            Log.e(MYTAG, "In getIndex, index[0] = " + index);
//            //this.textView.setText("In getIndex, index[0] = " + index + ", removed[0] = " + removed);
//            return index;
//        }
//        if(!indexList.isEmpty()){
//            Iterator<Integer> iter = indexList.iterator();
//            while(iter.hasNext()){
//                int next = iter.next();
//                if(next > index)
//                    index = next;
//            }
//            indexList.add(index + 1);
//            Log.e(MYTAG, "in getIndex add index " + (index + 1));
//            this.textView.setText("in getIndex add index " + (index + 1));
//            return index + 1;
//        }
//        index = 0;
//        indexList.add(index);
//        return index;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast toast= Toast.makeText(getApplicationContext(), "here in onOptionItemSlelected ", Toast.LENGTH_SHORT);
        toast.show();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
