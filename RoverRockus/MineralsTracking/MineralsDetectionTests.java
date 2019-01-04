package org.firstinspires.ftc.teamcode.RoverRockus.MineralsTracking;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@TeleOp(name = "MineralsDetectionTests23")
@Disabled
public class MineralsDetectionTests extends LinearOpMode {

    //MOTORS
    private DcMotor motor1 = null;
    private DcMotor motorR = null;
    private DcMotor motorL = null;

    protected ModernRoboticsI2cGyro GyroSensor;
    protected ModernRoboticsI2cGyro GyroS;

    double zTotal;

    //VARIABLES
    char goldPos;

    //VUFORIA ASSETS
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private static final String VUFORIA_KEY = "AYy3kgb/////AAAAGfahknPNfEOHpk9drxT3x5s+h85enQDuwX5Y/R9chthrPe1AQ1A+iYyS9PoUpVOVcu4TM/lzJa/PqlyaHKJWh+fI63xLIftsjqQ15b+MoQNZrgG4sw0swD9/yYSfSn3AU6PuQ6OozHZf4zrEOiL2AL/1OMMxbd9KddgiIIX5X/rnx7VFMFiNR8vq+otCHameCqnzdRcCkp1rqo+bewMyMYjTeYIyl29wn0oElYjg1PdBoYgDiUIjQu4sVECgCH7c6+pmEYe37ypfeMCxoGmG60L8bUmq5RrzZ1mxdJkugZ4hRbG/UIm1aHApSHE+ljsAexK3crM78qRfdVK6B9PTsnEEq9C40FuYu/ZqcCglO5VZ\n";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        motor1 = hardwareMap.dcMotor.get("motor1");
        GyroSensor = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("GyroSensor");
        GyroS = (ModernRoboticsI2cGyro) GyroSensor;

        initDetector();

        telemetry.addData(">", "Gyro Calibrating. Do Not move!");
        telemetry.update();
        GyroSensor.calibrate();

        while (!isStopRequested() && GyroSensor.isCalibrating()) {
            sleep(500);
            idle();
        }

        telemetry.addData(">", "Gyro Calibrated.  Press Start.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            tfodActivate();
            while (opModeIsActive()) {
                mineralDetection();
                knockOffMineral();
            }
        }
        tfodShutdown();
    }

    //METHODS
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
    private void initDetector() {
        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
    }
    private void mineralDetection() {
        if (tfod != null) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() == 2) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                        }
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            if (goldMineralX != -1 && silverMineral1X != -1) {
                                if (goldMineralX < silverMineral1X) {
                                    goldPos = 'L';
                                } else if (goldMineralX > silverMineral1X) {
                                    goldPos = 'C';
                                }
                            }
                        } else if(goldMineralX == -1){
                            goldPos = 'R';
                        }
                    }
                }else{
                    goldPos='n';
                }
            }
        }
    }
    private void knockOffMineral(){
        telemetry.addData("goldPos: ",goldPos);
        telemetry.update();
        sleep(1500);
            switch (goldPos){
                case 'L': //CUBE: LEFT
                    while(GyroSensor.getIntegratedZValue() < 45){
                        motorR.setPower(1);
                        motorL.setPower(-1);
                        telemetry.addData("Grados: ", GyroSensor.getIntegratedZValue());
                        telemetry.update();
                    }
                    break;
                case 'R': //CUBE: RIGHT
                    while(GyroSensor.getIntegratedZValue() < 110){
                        motorR.setPower(1);
                        motorL.setPower(-1);
                        telemetry.addData("Grados: ", GyroSensor.getIntegratedZValue());
                        telemetry.update();
                    }
                    break;
                case 'C': //CUBE: CENTER
                    while(GyroSensor.getIntegratedZValue() < 90){
                        motorR.setPower(1);
                        motorL.setPower(-1);
                        telemetry.addData("Grados: ", GyroSensor.getIntegratedZValue());
                        telemetry.update();
                    }
                    break;
                case 'n': //IF THE ROBOT DOESNT FIND EXACTLY 2 OBJECTS
                    motor1.setPower(0);
                    break;
            }
        motorR.setPower(0);
        motorL.setPower(0);
        telemetry.addData("Grados: ", GyroSensor.getIntegratedZValue());
        telemetry.update();
        sleep(1500);
        motorR.setPower(1);
        motorL.setPower(1);
        sleep(2000);
        motorR.setPower(0);
        motorL.setPower(0);
        }
    private void tfodActivate () {
        if (tfod != null) {
            tfod.activate();
        }
    }
    private void tfodShutdown () {
        if (tfod != null) {
            tfod.shutdown();
        }
    }
}
