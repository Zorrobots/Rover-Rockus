package org.firstinspires.ftc.teamcode.RoverRockus.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "TeleOp_2018")
//@Disabled
public class TeleOp_2018 extends OpMode {

    Servo servo1;
    Servo servo2;

    DcMotor wRF;
    DcMotor wLF;
    DcMotor wRB;
    DcMotor wLB;

    DcMotor arm;
    DcMotor elbow;
    DcMotor lifter1;
    DcMotor lifter2;

    @Override
    public void init() {
        servo1 = hardwareMap.servo.get("servo1");
        servo2 = hardwareMap.servo.get("servo2");
        wRF = hardwareMap.dcMotor.get("wRF");
        wLF = hardwareMap.dcMotor.get("wLF");
        wRB = hardwareMap.dcMotor.get("wRB");
        wLB = hardwareMap.dcMotor.get("wLB");
        arm = hardwareMap.dcMotor.get("arm");
        elbow = hardwareMap.dcMotor.get("elbow");
        lifter1 = hardwareMap.dcMotor.get("lifter1");
        lifter2 = hardwareMap.dcMotor.get("lifter2");

        wRF.setDirection(DcMotor.Direction.REVERSE);
        wRB.setDirection(DcMotor.Direction.REVERSE);
        lifter2.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        wRF.setPower(gamepad1.right_stick_y);
        wRB.setPower(gamepad1.right_stick_y);
        wLF.setPower(gamepad1.left_stick_y);
        wLB.setPower(gamepad1.left_stick_y);

        arm.setPower(gamepad2.left_stick_y);
        elbow.setPower(gamepad2.right_stick_y);

        if (gamepad2.a){
            servo1.setPosition(1);
        }else if (gamepad2.b){
            servo1.setPosition(.5);
        }else if (gamepad2.y){
            servo2.setPosition(1);
        }else if (gamepad2.x){
            servo2.setPosition(.5);
        }else{
            servo1.setPosition(0);
            servo2.setPosition(0);
        }

        if (gamepad2.right_trigger > 0){
            lifter1.setPower(gamepad2.right_trigger);
            lifter2.setPower(gamepad2.right_trigger);
        }else if (gamepad2.left_trigger > 0){
            lifter1.setPower(-gamepad2.left_trigger);
            lifter2.setPower(-gamepad2.left_trigger);
        }else{
            lifter1.setPower(0);
            lifter2.setPower(0);
            /*lifter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lifter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);*/
        }
    }
}