package com.example.vivid.myjisuanqi;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 需要注意的问题
 *
 * 1输入了 不符合运算规则数目的运算符 和小数点 比如 1..1+2 等 1++2  用两个关键字来判断属否符合规范了
 *
 * 2小数点后面没数字去运算了 1.+2 判断后为其加一个0 使之变成 1.0+2 提高用户体验
 *
 * 3 当已经 输入了 如3+2 时再输入运算符 则 吧前面的数据先计算了 在结果上添加运算符
 *
 * 4如果是 3+ 后面再输入运算符则 替换前面的运算符  小数点不允许在一个数值上出现多次
 *
 * 5若没有数据直接输入运算符则第一个数为0
 *
 * 如果 直接输入 . 则将其补充为 0.
 *
 * 细节 体验
 * 由于此处是用空格来分隔运算符和数据 所以在删除时 碰到运算符也就是遇到空格 则要删掉后面三个字符
 *
 * 最后显示结果时 不要出现 .000  也就是删掉无用的小数未
 *
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //错误信息 除数不能为0
    public final String BEI_CHU_SHU_NOT_ZERO="除数不能为0";
//日志 快捷键 logt
    private static final String TAG = "MainActivity";

    //用于判断是否已经经过计算了 计算过值变为true 重置后又变flase
    // 主要用于 计算结果后 在进行计算 如果输入 运算符继续计算 输入数字则清空原有结果
    private boolean isJiSuan=false;
//    用于判断是否已近添加运算符 用于消除多个运算符带来的影响
    private boolean haveYunSuanFu=false;
//    用于判断是否这个运算的小数点 是否已经合理匹配了  则不能再添加小数点
//    当添加运算符 或者在计算的话
    private boolean haveXiaoShuDian=false;

//    文本框中的内容
    public String msg;

//    实例化按钮
    Button btn_0;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;
    Button btn_6;
    Button btn_7;
    Button btn_8;
    Button btn_9;
    Button btn_dian;
    Button btn_cheng;
    Button btn_chu;
    Button btn_jia;
    Button btn_jian;
    Button btn_c;
    Button btn_del;
    Button btn_deng;

    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        实例化按钮
        btn_0= (Button) findViewById(R.id.button_0);
        btn_1= (Button) findViewById(R.id.button_1);
        btn_2= (Button) findViewById(R.id.button_2);
        btn_3= (Button) findViewById(R.id.button_3);
        btn_4= (Button) findViewById(R.id.button_4);
        btn_5= (Button) findViewById(R.id.button_5);
        btn_6= (Button) findViewById(R.id.button_6);
        btn_7= (Button) findViewById(R.id.button_7);
        btn_8= (Button) findViewById(R.id.button_8);
        btn_9= (Button) findViewById(R.id.button_9);
        btn_dian= (Button) findViewById(R.id.button_dian);
        btn_cheng= (Button) findViewById(R.id.button_cheng);
        btn_chu= (Button) findViewById(R.id.button_chu);
        btn_jia= (Button) findViewById(R.id.button_jia);
        btn_jian= (Button) findViewById(R.id.button_jian);
        btn_c= (Button) findViewById(R.id.button_c);
        btn_del= (Button) findViewById(R.id.button_del);
        btn_deng= (Button) findViewById(R.id.button_deng);

//        显示框
        et= (EditText) findViewById(R.id.et_input);

//        设置点击事件
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_dian.setOnClickListener(this);
        btn_cheng.setOnClickListener(this);
        btn_chu.setOnClickListener(this);
        btn_jia.setOnClickListener(this);
        btn_jian.setOnClickListener(this);
        btn_c.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_deng.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        //获取文本框内容
        msg=et.getText().toString();
//        如果显示的是错误信息则清空
        if (msg.equals(BEI_CHU_SHU_NOT_ZERO)){
            msg="";
//            清空数据
            qingKon();
        }
        switch (v.getId()){
            case R.id.button_0:
            case R.id.button_1:
            case R.id.button_2:
            case R.id.button_3:
            case R.id.button_4:
            case R.id.button_5:
            case R.id.button_6:
            case R.id.button_7:
            case R.id.button_8:
            case R.id.button_9:
                addNum(v);
                break;
            case R.id.button_dian:
                addXiaoShuDian(v);
                break;
            case R.id.button_jia:
            case R.id.button_jian:
            case R.id.button_cheng:
            case R.id.button_chu:
                pinjie(v);
                break;
            case R.id.button_c:
                qingKon();
                break;
            case R.id.button_del:
                delete();
                break;
            case R.id.button_deng:
                getResult();
                break;

        }
    }

    /**
     * 添加数字
     *
     */
    public void addNum(View v){
        //                数值直接加在输入框后面
        if (isJiSuan==true&&haveYunSuanFu==false){
//                    如果已经进行运算 后面没有运算符则 清空写入数据
            et.setText(((Button)v).getText());
            isJiSuan=false;
            haveYunSuanFu=false;
            haveXiaoShuDian=false;
        }else {
//                    如果没有进行运算 或者有运算但是有运算符则 尾部追加
            et.setText(msg+((Button)v).getText());
        }
    }
    /**
     * 添加小数点
     */
    public void addXiaoShuDian(View v){
        if (!haveXiaoShuDian){
            //如果最后一位是空格（就是运算符） 或者msg为空 则在.之前加个0
            if (msg.endsWith(" ")||msg==null||msg.trim().equals("")){
                et.setText(msg+0+((Button)v).getText());
            }else {
                //                数值直接加在输入框后面
                et.setText(msg+((Button)v).getText());
//                加点就代表没运算符
            }
            isJiSuan=false;
            haveXiaoShuDian=true;
        }
    }
    /**
     * 清空按钮
     *
     */
    private void qingKon(){
        // 全部清空
        et.setText("");
//
        isJiSuan=false;
        haveYunSuanFu=false;
        haveXiaoShuDian=false;
    }
    /**
     * 删除按钮 当最后是运算符 则删除三个字符 运算符设为没有
     *          注意点（ 需要判断删完后有没有小数点）
     * 当最后第二位是 . 有于 3.1 删成3. 没意义 直接删两个 变成3 小数点设为没有
     *          注意点（msg.lastIndexOf(".") 当没有. 时结果为-1）
     * 正常情况下就删一个字符 如果删到小数点则haveXiaoShuDian=false;
     */
    public void delete(){
//        判断是否有数据
        if (msg!=null&&!msg.trim().equals("")){
            if (msg.endsWith(" ")){
                //截掉最后3位
                et.setText(msg.substring(0,msg.length()-3));
//               没有运算符
                haveYunSuanFu=false;
//                判断当前是否含有小数点
                if (et.getText().toString().trim().contains(".")){
                    haveXiaoShuDian=true;
                }else {
                    haveXiaoShuDian=false;
                }
            }else if (msg.lastIndexOf(".")!=-1&&msg.lastIndexOf(".")==(msg.length()-2)){
//                当最后第二位是小数点时 删除按钮直接删两个 把小数点也直接删了
                et.setText(msg.substring(0,msg.length()-2));
//                小数点就没了
                haveXiaoShuDian=false;

            }else {
                if (msg.length()>=1){
                    if (msg.charAt(msg.length()-1)=='.'){
                        haveXiaoShuDian=false;
                    }
                    //                正常情况下只删一个字符
                    et.setText(msg.substring(0,msg.length()-1));
                }
            }
        }
    }

    /**
     * 拼接运算的
     * @param v
     */
    private void pinjie(View v){
        // 获取被点击的按纽
        Button b= (Button) v;
        //          消除多个运算符带来的影响 如果连点运算符 则之前的运算符被替换
        if (haveYunSuanFu==true){
//           当最后一位是运算符时 替换运算符
            if (msg.endsWith(" ")){
                msg=msg.replaceAll("\\p{Blank}.\\p{Blank}"," "+b.getText()+" ");

                et.setText(msg);

            }else {
                //    当最后一位不是运算符时进行计算
                getResult();
                et.setText(et.getText()+" "+b.getText()+" ");
                //运算符设为有
                haveYunSuanFu=true;
                //小数点为没有
                haveXiaoShuDian=false;
                //由于加了运算符 所以是否运算要为flase
                isJiSuan=false;
            }
        }else {
            //            无运算符的情况下
            if (msg==null||msg.trim().equals("")){
                et.setText(0+" "+b.getText()+" ");
            }else {
//                空格只是为了便于区分 方便计算
                et.setText(msg+" "+b.getText()+" ");
            }
            //设置已近添加运算符了
            haveYunSuanFu=true;
//            添加运算符后小数点为 无
            haveXiaoShuDian=false;
        }

    }

//

    /**
     * 运算
     */
    private void getResult(){
        //定义结果
        double result=0;
        //获取文本框内容
        String msg=et.getText().toString();

        //如果没有数据则不做运算
        if (msg==null&&msg.trim().equals("")){
           return;
        }

//     判断是否有空格 有空格代表有运算符+-*/ 没空格呢就没运算直接返回
        if (!msg.contains(" ")){
            return;
        }

//        运算符前面的字符
        String s1=msg.substring(0,msg.indexOf(" "));
//       运算符 由于带了空格 所以后面判断时要加trim() 方法
        String op=msg.substring(msg.indexOf(" "),msg.indexOf(" ")+2);
//        运算符后面的数据
        String s2=msg.substring(msg.indexOf(" ")+3);

//        if (s1.trim().equals(".")||s2.trim().equals(".")){
//            qingKon();
//        }

//        Log.i(TAG, "getResult: ----" + s1 +"  "+ s2 +"  " + op);

// 只有或没有第一位数 和运算符 第二位数没有则不予以运算
        if (s2.equals("")||s2==null){
            return;
        }
//        当s1为空 s2不为空 则让s1="0"
        if (s1.trim().equals("")||s1==null){
            s1="0";
        }
//      当都不为空则进行计算
//        if(s1!=null&&s2!=null&&!s1.trim().equals("")&&!s2.trim().equals("")){
//            转换为数字
            double d1=Double.parseDouble(s1);
            double d2=Double.parseDouble(s2);
//            更据运算符进行计算
            if (op.trim().equals("+")){
                result=d1+d2;
            }else if (op.trim().equals("-")){
                result=d1-d2;
            }else if (op.trim().equals("*")){
                result=d1*d2;
            }else if (op.trim().equals("/")){
//                除数不能为0
                if (d2==0){
                    et.setText(BEI_CHU_SHU_NOT_ZERO);
                    return;
                }
                result=d1/d2;
            }
//        }
//        Log.i(TAG, "getResult: result1="+result);
        //判断结果小数点后面是不是都是0  .00 小数点后面的去除0
        if (result==((int)result)){
           int i=(int)result;
            //结果显示与界面上
            et.setText(String.valueOf(i));
            //设为没小数点
            haveXiaoShuDian=false;
        }else {
            //结果显示与界面上
            et.setText(String.valueOf(result));
            //设为有小数点
            haveXiaoShuDian=false;
        }
//        是否运算
       isJiSuan=true;
//        无运算符运算符
        haveYunSuanFu=false;
    }
}
