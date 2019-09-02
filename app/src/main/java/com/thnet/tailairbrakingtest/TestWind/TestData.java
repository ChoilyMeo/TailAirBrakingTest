package com.thnet.tailairbrakingtest.TestWind;

import android.util.Log;

import com.thnet.tailairbrakingtest.DAO.PressureValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestData {
    private static final String TAG = TestData.class.getSimpleName();
    private String _TestIndex = "";
    private String _TestDate = "";
    private String _startTime = "";
    private String _endTime = "";
    private String _CheCi = "";
    private String _GuDao = "";
    private String _Liangshu = "";
    private String _DingYa = "";
    private String _ShiFengLeiXing = "";
    private String _ZXQBH = "";
    private String _SCJBH = "";
    private String _Ban = "";
    private String _Zu = "";
    private String _KeLieWeiID1 = "";
    private String _KeLieWeiID2 = "";
    private String _BeiYongKeLieWeiID = "";
    private byte _cmdParam;//作业区别：有无列尾、有无计算机、客货车

    public Test_GD _GDSY;
    public Test_AD _ADSY;
    public Test_BY _BYSY;
    public Test_JL _JLSY;
    public Test_ZFLX _ZFLX;
    public Test_JGHJ _JGHJ;
    public Test_JNLX _JNLX;
    public Test_JNGD _JNGD;
    public Test_JNAD _JNAD;
    public List<PressureValue> lstPressureValue = new ArrayList<PressureValue>();
    public List<TestContent> listTest = new ArrayList<TestContent>();

    SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");

    public TestData() {
        _GDSY = new Test_GD();
        _ADSY = new Test_AD();
        _BYSY = new Test_BY();
        _JLSY = new Test_JL();
        _ZFLX = new Test_ZFLX();
        _JGHJ = new Test_JGHJ();
        _JNLX = new Test_JNLX();
        _JNGD = new Test_JNGD();
        _JNAD = new Test_JNAD();
        listTest = new ArrayList<TestContent>(0);
        listTest.add(_GDSY);
        listTest.add(_ADSY);
        listTest.add(_BYSY);
        listTest.add(_JLSY);
        listTest.add(_ZFLX);
        listTest.add(_JNLX);
        listTest.add(_JNGD);
        listTest.add(_JGHJ);
        listTest.add(_JNAD);
        lstPressureValue = new ArrayList<PressureValue>(0);
        _TestDate = formatterDate.format(new Date(System.currentTimeMillis()));
    }

    public void BeginTest() {
        Date curDate = new Date(System.currentTimeMillis());
        _TestDate = formatterDate.format(curDate);
        _startTime = formatterTime.format(curDate);
    }

    public void EndTest() {
        _endTime = formatterTime.format(new Date(System.currentTimeMillis()));
    }

    public TestData(String line, String trainNo, String trainCount, String specifyPressureValue, String testKind, String handDeviceNo, String deviceNo,String operatorClass, String operatorGroup, String klw1, String klw2, String klwby) {
        this();
        _CheCi = trainNo;
        _GuDao = line;
        _Liangshu = trainCount;
        _DingYa = specifyPressureValue;
        _ShiFengLeiXing = testKind;
        _SCJBH = handDeviceNo;
        _ZXQBH = deviceNo;
        _KeLieWeiID1 = klw1;
        _KeLieWeiID2 = klw2;
        _BeiYongKeLieWeiID = klwby;
        _Ban = operatorClass;
        _Zu = operatorGroup;
    }

    public TestData(String testIndex, String line, String trainNo, String trainCount, String specifyPressureValue, String testKind, String handDeviceNo,String operatorClass, String operatorGroup, String deviceNo, String klw1, String klw2, String klwby) {
        this(line, trainNo, trainCount, specifyPressureValue, testKind, handDeviceNo, deviceNo, operatorClass, operatorGroup, klw1, klw2, klwby);
        _TestIndex = testIndex;
    }

    public TestContent GetTestByName(String strTestName) {
        TestContent currTest = null;
        for (TestContent t : listTest) {
            if (t.get_testName().equals(strTestName)) {
                currTest = t;
            }
        }
        return currTest;
    }

    public TestContent GetTestByCommand(int cmd) {
        TestContent currTest = null;
        for (TestContent t : listTest) {
            if (t.get_TestCommandCode().getValue() == cmd) {
                currTest = t;
            }
        }
        return currTest;
    }
//    public String update(){
//        if (String.IsNullOrEmpty(_TestIndex))
//        {
//            //数据的插入操作
//            String strBH = DateTime.Now.ToString("yyyyMMdd") + "0001";
//            String sql = "select max(BH) from TestWindContent where BH like '" + DateTime.Now.ToString("yyyyMMdd") + "%'";
//            DataSet ds = TipSoundPlayer.ExeSql(sql);
//            if (ds != null && ds.Tables.Count > 0 && ds.Tables[0].Rows.Count > 0)
//            {
//                String bh_old = ds.Tables[0].Rows[0][0].ToString();
//                if (!String.IsNullOrEmpty(bh_old))
//                {
//                    int nxh = Convert.ToInt32(bh_old.Substring(8, 4)) + 1;
//                    String xh = nxh.ToString();
//                    strBH = bh_old.Substring(0, 8) + xh.PadLeft(4, '0');
//                }
//            }
//            List<String> lsql = new List<String>();
//            lsql.Add("insert into TestWindContent values('" + strBH + "','" + _TestDate + "','" + _CheCi + "','" + _Liangshu + "','" + _DingYa + "','" + _startTime + "','" + _endTime + "','" + _ShiFengLeiXing + "','" + _GuDao + "','" + SysParms.TestOperatorName + "','" + SysParms.TestOperator + "','" + _ZXQBH + "')");
//            _JNAD.JYSD = 0;
//            _JNGD.JYSD = 0;
//            _ADSY.JYSJ = 0;
//            _GDSY.JYSJ = 0;
//            foreach (CTestContent tst in listTest)
//            {
//                if (tst.Stat != CTestContent.TestState.tsNotBegin && tst.Stat != CTestContent.TestState.tsNotSelected)
//                {
//                    lsql.Add("insert into TestDetail values('" + strBH + "','" + tst.TestName + "','" + tst.StartTime + "','" + tst.EndTime + "','" + tst.ZGYL + "','" + tst.BYSJ + "','" + tst.StrLXL + "','" + tst.JYL + "','" + tst.JYSJ + "','" + tst.JYSD + "','" + tst.State + "')");
//                }
//            }
//            for (int i = 0; i < lstPressureValue.Count; i++)
//            {
//                lsql.Add("insert into PressureValue values('" + strBH + "','" + lstPressureValue[i].PressureTime + "','" + lstPressureValue[i].PressureValue.ToString() + "','" +
//                        lstPressureValue[i].HeadPressureValue.ToString() + "','" + lstPressureValue[i].SourcePressureValue.ToString() + "','" +
//                        lstPressureValue[i].CenterPressureValue.ToString() + "','" + lstPressureValue[i].BoxTemperature.ToString() + "','" + lstPressureValue[i].BoxHumidity.ToString() + "')");
//            }
//            int rtn = TipSoundPlayer.BatchExecSql(lsql);
//            if (rtn == 0)
//            {
//                _TestIndex = strBH;
//            }
//        }
//        else
//        {
//        }
//        return _TestIndex;
//    }

    public String get_TestIndex() {
        return _TestIndex;
    }

    public void set_TestIndex(String _TestIndex) {
        this._TestIndex = _TestIndex;
    }

    public String get_TestDate() {
        return _TestDate;
    }

    public void set_TestDate(String _TestDate) {
        this._TestDate = _TestDate;
    }

    public String get_startTime() {
        return _startTime;
    }

    public void set_startTime(String _startTime) {
        this._startTime = _startTime;
    }

    public String get_endTime() {
        return _endTime;
    }

    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
    }

    public String get_CheCi() {
        return _CheCi;
    }

    public void set_CheCi(String _CheCi) {
        this._CheCi = _CheCi;
    }

    public String get_GuDao() {
        return _GuDao;
    }

    public void set_GuDao(String _GuDao) {
        this._GuDao = _GuDao;
    }

    public int get_GuDaoInt() {
        int n = 0;
        try {
            n = Integer.parseInt(this._GuDao);
        } catch (Exception ex) {
            Log.i(TAG, "股道转换失败。");
            n = 0;
        }
        return n;
    }

    public String get_Liangshu() {
        return _Liangshu;
    }

    public void set_Liangshu(String _Liangshu) {
        this._Liangshu = _Liangshu;
    }

    public int get_LiangshuInt() {
        int n = 0;
        try {
            n = Integer.parseInt(this._Liangshu);
        } catch (Exception ex) {
            Log.i(TAG, "辆数转换失败");
            n = 0;
        }
        return n;
    }

    public String get_DingYa() {
        return _DingYa;
    }

    public void set_DingYa(String _DingYa) {
        this._DingYa = _DingYa;
    }

    public int get_DingYaInt() {
        int n = 0;
        try {
            n = Integer.parseInt(this._DingYa);
        } catch (Exception ex) {
            Log.i(TAG, "定压转换失败。");
            n = 0;
        }
        return n;
    }

    public String get_ShiFengLeiXing() {
        return _ShiFengLeiXing;
    }

    public void set_ShiFengLeiXing(String _ShiFengLeiXing) {
        this._ShiFengLeiXing = _ShiFengLeiXing;
    }

    public String get_ZXQBH() {
        return _ZXQBH;
    }

    public void set_ZXQBH(String _ZXQBH) {
        this._ZXQBH = _ZXQBH;
    }

    public int get_ZXQBHInt() {
        int n = 0;
        try {
            n = Integer.parseInt(this._ZXQBH);
        } catch (Exception ex) {
            n = 0;
        }
        return n;
    }

    public String get_SCJBH() {
        return _SCJBH;
    }

    public void set_SCJBH(String _SCJBH) {
        this._SCJBH = _SCJBH;
    }

    public String get_Ban() {
        return _Ban;
    }

    public void set_Ban(String _Ban) {
        this._Ban = _Ban;
    }

    public String get_Zu() {
        return _Zu;
    }

    public void set_Zu(String _Zu) {
        this._Zu = _Zu;
    }

    public String get_KeLieWeiID1() {
        return _KeLieWeiID1;
    }

    public void set_KeLieWeiID1(String _KeLieWeiID1) {
        this._KeLieWeiID1 = _KeLieWeiID1;
    }

    public String get_KeLieWeiID2() {
        return _KeLieWeiID2;
    }

    public void set_KeLieWeiID2(String _KeLieWeiID2) {
        this._KeLieWeiID2 = _KeLieWeiID2;
    }

    public String get_BeiYongKeLieWeiID() {
        return _BeiYongKeLieWeiID;
    }

    public void set_BeiYongKeLieWeiID(String _BeiYongKeLieWeiID) {
        this._BeiYongKeLieWeiID = _BeiYongKeLieWeiID;
    }

    public byte get_cmdParam() {
        return _cmdParam;
    }

    public void set_cmdParam(byte _cmdParam) {
        this._cmdParam = _cmdParam;
    }
}
