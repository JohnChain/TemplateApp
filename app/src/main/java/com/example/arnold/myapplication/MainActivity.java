package com.example.arnold.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.util.Log;
import android.widget.Toast;
import android.net.Uri;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Button changeB;
    public Button addB;
    public Button saveB;
    public TableLayout parentTable;
    public TextView totalTV;

    private DBHelper DBHP;

//    private List<Integer> indexList = new ArrayList<Integer>();
//    private List<Integer> reservedList = new ArrayList<Integer>();
    public boolean changing = false;
    private final String MYTAG = "johnchain";

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

        this.DBHP = new DBHelper(this);
        Cursor cursor = this.DBHP.select_all();
        int count = cursor.getCount();
        for(int i = count; i < count; i++){
            cursor.moveToPosition(i);
            String item = cursor.getString(0);
            float price = cursor.getFloat(1);
            addItem(item, price);
        }
        cursor.close();
    }

    public boolean onSaveItem(){
        // TODO：
        // 1：将所有可编辑项设为 disable状态
        // 2：将编辑项的值存储

        int total = 0;
        int count = parentTable.getChildCount();
        for(int i = 0; i < count;i++){
            TableRow subRow = (TableRow)parentTable.getChildAt(i);
            ((Button)subRow.findViewById(R.id.deleteB1)).setEnabled(false);
            ((EditText)subRow.findViewById(R.id.itemET1)).setEnabled(false);
            ((EditText)subRow.findViewById(R.id.priceET1)).setEnabled(false);

            EditText temp = (EditText) subRow.findViewById(R.id.numberET1);
            if(temp == null){
                Log.e(MYTAG, "temp is null");
            }else {
                String numberS = temp.getText().toString();
                if(!numberS.equals("")) {
                    total += Integer.parseInt(numberS);
                }
            }
        }
        this.changing = false;
        Log.e(MYTAG, "total momey = " + total);
        this.totalTV.setText("" + total);

        //TODO:
        // will save data below
        this.DBHP.delete_all();
        for(int i = 0; i < count;i++){
            TableRow subRow = (TableRow)parentTable.getChildAt(i);
            String item = ((EditText)subRow.findViewById(R.id.itemET1)).getText().toString();
            String priceString = ((EditText) subRow.findViewById(R.id.priceET1)).getText().toString();
            float price = 0;
            if(!priceString.equals("") && !item.equals("")) {
                price = Float.parseFloat(priceString);
                this.DBHP.insert(item, price);
            }else{
                Log.e(MYTAG, "item and price cannot be null");
                Toast toast = Toast.makeText(getApplicationContext(), "项目和单价不能为空", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        Log.e(MYTAG, "insert finished, query below");
        Cursor cursor = this.DBHP.select_all();
        int rows = cursor.getCount();
        for(int i = 0; i < rows; i++){
            cursor.moveToPosition(i);
            String item = cursor.getString(0);
            float price = cursor.getFloat(1);
            Log.e(MYTAG, "In select row: " + i + ": item: " + item + " price: " + price);
        }

        cursor.close();
        return true;
    }

    public boolean onDeleteItem(TableRow subRow){
        // TODO:
        // 通过所点按钮找到其所在RowTable，然后删除该RowTable内所有控件及其本身
        //
        int index = parentTable.indexOfChild(subRow);
        parentTable.removeView(subRow);
        Log.e(MYTAG, "index = " + index + "removed");

        // ADD: test MO, MT, SMS, PBR,
        Toast toast= Toast.makeText(getApplicationContext(), "will delete SubRowTable ", Toast.LENGTH_SHORT);
        toast.show();
        String number = "10086";
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ number));
        //startActivity(intent);
        return true;
    }

    public boolean onChangeItem(){
        if(this.changing == true) {
            return true;
        }
        int total = 0;
        int count = parentTable.getChildCount();
        for(int i = 0; i < count;i++){
            TableRow subRow = (TableRow)parentTable.getChildAt(i);
            ((Button)subRow.findViewById(R.id.deleteB1)).setEnabled(true);
            ((EditText)subRow.findViewById(R.id.itemET1)).setEnabled(true);
            ((EditText)subRow.findViewById(R.id.priceET1)).setEnabled(true);
        }
        this.changing = true;
        return true;
    }

    public boolean onAddItem(){
        // 获取一个可用的RowTable的序号
        //final int index = getIndex();
        // create subtable contents
        Button deleteB = new Button(this);
        deleteB.setText(R.string.delete);
        deleteB.setId(R.id.deleteB1);
        //delete.setEnabled(false);
        //final TableLayout localParentTable = parentTable;
        deleteB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(MYTAG, "Clicked Button parent type is: " + v.getParent().getClass().toString());
                onDeleteItem((TableRow) (v.getParent()));
            }
        });

        TextView itemTV = new TextView(this);
        //itemTV.setText(R.string.item + index);
        itemTV.setText(R.string.item);

        EditText itemET = new EditText(this);
        itemET.setId(R.id.itemET1);

        TextView priceTV = new TextView(this);
        priceTV.setText(R.string.price);

        EditText priceET = new EditText(this);
        priceET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式
        priceET.setId(R.id.priceET1);

        TextView numberTV = new TextView(this);
        numberTV.setText(R.string.number);

        EditText numberET = new EditText(this);
        numberET.setId(R.id.numberET1);
        numberET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式

        TableRow subTable = new TableRow(this);
        subTable.addView(deleteB);
        subTable.addView(itemTV);
        subTable.addView(itemET);
        subTable.addView(priceTV);
        subTable.addView(priceET);
        subTable.addView(numberTV);
        subTable.addView(numberET);

        parentTable.addView(subTable);

        return true;
    }

    public boolean addItem(String item, float price){
        // 获取一个可用的RowTable的序号
        //final int index = getIndex();
        // create subtable contents
        Button deleteB = new Button(this);
        deleteB.setText(R.string.delete);
        deleteB.setId(R.id.deleteB1);
        deleteB.setEnabled(false);
        //final TableLayout localParentTable = parentTable;
        deleteB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(MYTAG, "Clicked Button parent type is: " + v.getParent().getClass().toString());
                onDeleteItem((TableRow) (v.getParent()));
            }
        });

        TextView itemTV = new TextView(this);
        //itemTV.setText(R.string.item + index);
        itemTV.setText(R.string.item);

        EditText itemET = new EditText(this);
        itemET.setId(R.id.itemET1);
        itemET.setText(item);
        itemET.setEnabled(false);

        TextView priceTV = new TextView(this);
        priceTV.setText(R.string.price);

        EditText priceET = new EditText(this);
        priceET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式
        priceET.setId(R.id.priceET1);
        priceET.setText("" + price);
        priceET.setEnabled(false);

        TextView numberTV = new TextView(this);
        numberTV.setText(R.string.number);

        EditText numberET = new EditText(this);
        numberET.setId(R.id.numberET1);
        numberET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式

        TableRow subTable = new TableRow(this);
        subTable.addView(deleteB);
        subTable.addView(itemTV);
        subTable.addView(itemET);
        subTable.addView(priceTV);
        subTable.addView(priceET);
        subTable.addView(numberTV);
        subTable.addView(numberET);

        parentTable.addView(subTable);
        return true;
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
