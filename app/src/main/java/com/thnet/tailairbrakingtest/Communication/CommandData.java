package com.thnet.tailairbrakingtest.Communication;

import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;
import com.thnet.tailairbrakingtest.Utility.HexUtil;

public class CommandData {
    public int command = 0;//指令
    public int protocelKind;//协议类别，1数据 or 0指令
    public int testStatus;//试验状态（步骤），对应CTestWindProtocel.TestStatus中的值
    public byte cmdParam;
    public int specifiedPressure;//定压 500 或者 600

    public CommandData(){
        this.command = CTestWindProtocel.TestCommand.TestBegin.getValue();
        this.protocelKind = 0;
        this.testStatus = 0;
        this.cmdParam = 0;
        this.specifiedPressure = SysParamsAll.SpecifiedPressure600;
    }
    public int GetNoTail(){
        return HexUtil.GetBit(cmdParam, HexUtil.BIT_EIGHT);
    }
    public int GetHasComputer(){
        return HexUtil.GetBit(cmdParam, HexUtil.BIT_SEVEN);
    }
    public int GetPassengerOrFreightTrains(){
        return HexUtil.GetBit(cmdParam, HexUtil.BIT_ONE);
    }
    public void GetCommandFromByte(byte cmdByte){
        protocelKind = HexUtil.GetBit(cmdByte, HexUtil.BIT_ONE);//首位：0 指令、1 数据
        if (HexUtil.GetBit(cmdByte, HexUtil.BIT_TWO) > 0) {//第2位：0 定压500、1 定压600
            specifiedPressure = SysParamsAll.SpecifiedPressure600;
        } else {
            specifiedPressure = SysParamsAll.SpecifiedPressure500;
        }
        command = HexUtil.GetBits(cmdByte, HexUtil.BIT_FOUR, HexUtil.BIT_RANGE_FIVE);//第4-8位：具体的试风指令
    }
    public byte MergerTestCommand() {
        byte commandByte;
        if (specifiedPressure == SysParamsAll.SpecifiedPressure500) {
            commandByte = (byte) (0x20 + command);
        } else {
            commandByte = (byte) (0x60 + command);
        }
        return commandByte;
    }
    public static byte MergerTestCommand(int cmd, int specifiedPressure) {
        byte commandByte;
        if (specifiedPressure == SysParamsAll.SpecifiedPressure500) {
            commandByte = (byte) (0x20 + cmd);
        } else {
            commandByte = (byte) (0x60 + cmd);
        }
        return commandByte;
    }
    public static CommandData GetCommandFromCmdByte(byte cmdByte) {
        CommandData commandData = new CommandData();
        commandData.protocelKind = HexUtil.GetBit(cmdByte, HexUtil.BIT_ONE);//首位：0 指令、1 数据
        if (HexUtil.GetBit(cmdByte, HexUtil.BIT_TWO) > 0) {//第2位：0 定压500、1 定压600
            commandData.specifiedPressure = SysParamsAll.SpecifiedPressure600;
        } else {
            commandData.specifiedPressure = SysParamsAll.SpecifiedPressure500;
        }
        commandData.command = HexUtil.GetBits(cmdByte, HexUtil.BIT_FOUR, HexUtil.BIT_RANGE_FIVE);//第4-8位：具体的试风指令
        return commandData;
    }
}
