package com.example.arnold.myapplication;

import android.content.Intent;
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
    public Button deleteB1;
    public TableLayout parentTable;
    public TextView textView;

    public boolean changing = false;

    private List<Integer> indexList = new ArrayList<Integer>();
    private List<Integer> reservedList = new ArrayList<Integer>();

    private final String MYTAG = "johnchain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.changeB = new Button(this);
        this.addB = new Button(this);
        this.saveB = new Button(this);
        this.deleteB1 = new Button(this);
        this.parentTable = new TableLayout(this);
        this.textView = new TextView(this);

        changeB = (Button)findViewById(R.id.changeB);
        addB = (Button)findViewById(R.id.addB);
        saveB = (Button)findViewById(R.id.saveB);
        deleteB1 = (Button)findViewById(R.id.deleteB1);
        parentTable = (TableLayout)findViewById(R.id.parentTable);
        textView = (TextView)findViewById(R.id.textView);

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
    }

    public boolean onSaveItem(){
        // TODO：
        // 1：将所有可编辑项设为 disable状态
        // 2：将编辑项的值存储

        Iterator<Integer> iter = indexList.iterator();
        while(iter.hasNext()){
            TableRow subRow = (TableRow)parentTable.getChildAt(iter.next());
            ((Button)subRow.findViewById(R.id.deleteB1)).setEnabled(false);
            ((EditText)subRow.findViewById(R.id.itemET1)).setEnabled(false);
            ((EditText)subRow.findViewById(R.id.priceET1)).setEnabled(false);
        }

        int total = 0;
        int count = parentTable.getChildCount();
        for(int i = 0; i < count; i++){
            total += Integer.parseInt(((EditText) parentTable.getChildAt(i).findViewById(R.id.numberET1)).getText().toString());
        }
        Log.e(MYTAG, "total momey = " + total);
        this.changing = false;
        return true;
    }

    public boolean onDeleteItem(TableRow subRow){
        // TODO:
        // 通过所点按钮找到其所在RowTable，然后删除该RowTable内所有控件及其本身
        //
        int index = parentTable.indexOfChild(subRow);
        parentTable.removeView(subRow);
        Log.e(MYTAG, "index = " + index + "removed");
        indexList.remove((Integer) index);
        reservedList.add((Integer) index);
        Log.e(MYTAG, "indexList = " + indexList + " | reservedList = " + reservedList);

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
        Iterator<Integer> iter = indexList.iterator();
        while(iter.hasNext()){
            TableRow subRow = (TableRow)parentTable.getChildAt(iter.next());
            ((Button)subRow.findViewById(R.id.deleteB1)).setEnabled(true);
            ((EditText)subRow.findViewById(R.id.itemET1)).setEnabled(true);
            ((EditText)subRow.findViewById(R.id.priceET1)).setEnabled(true);
        }
        this.changing = true;
        return true;
    }

    public int onAddItem(){
        // 获取一个可用的RowTable的序号
        final int index = getIndex();
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
        itemTV.setText("项目" + index);

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
        numberET.setKeyListener(new DigitsKeyListener(false, true));  //设置文本编辑框仅数字模式

        TableRow subTable = new TableRow(this);
        subTable.addView(deleteB);
        subTable.addView(itemTV);
        subTable.addView(itemET);
        subTable.addView(priceTV);
        subTable.addView(priceET);
        subTable.addView(numberTV);
        subTable.addView(numberET);

        parentTable.addView(subTable, index);
        Log.e(MYTAG, "indexList = " + indexList + " | reservedList = " + reservedList);
        return index;
    }

    // 获取与一个可用的index， 用于新建的RowTable
    private int getIndex(){
        int index = 0;
        if(!reservedList.isEmpty()){
            index = reservedList.remove(0);
            indexList.add(index);
            Log.e(MYTAG, "In getIndex, index[0] = " + index);
            //this.textView.setText("In getIndex, index[0] = " + index + ", removed[0] = " + removed);
            return index;
        }
        if(!indexList.isEmpty()){
            Iterator<Integer> iter = indexList.iterator();
            while(iter.hasNext()){
                int next = iter.next();
                if(next > index)
                    index = next;
            }
            indexList.add(index + 1);
            Log.e(MYTAG, "in getIndex add index " + (index + 1));
            this.textView.setText("in getIndex add index " + (index + 1));
            return index + 1;
        }
        index = 0;
        indexList.add(index);
        return index;
    }

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
