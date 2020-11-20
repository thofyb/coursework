package com.example.staticization

import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.example.navitaslite.CpuCoreCluster
import com.example.navitaslite.MeasurementTool
import com.example.navitaslite.PowerProfile

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    var mResultTextView: TextView? = null
    var mStartTestButton: Button? = null
    var TEST_NUMBER = 15

    var MIN_FREQ = 400000
    var MAX_FREQ = 1500000

    companion object {
        var TAG = "STATICIZATION"
        var MAX_CYCLES = 1000
        var TEST_i = 10000

        @Volatile
        var TOTAL_ENERGY = 0f
        var CORE = 0
        var MASK = Math.pow(2.0, CORE.toDouble()).toInt()
        var CURR_ATTEMPT = 0
        var PID = 0
        var isOptimized = 0
        private val PROFILE: PowerProfile = standPowerProfile
        private val standPowerProfile: PowerProfile
            private get() {
                val cluster = CpuCoreCluster(4)
                cluster.speeds.add(1500000L)
                cluster.speeds.add(1400000L)
                cluster.speeds.add(1300000L)
                cluster.speeds.add(1200000L)
                cluster.speeds.add(1100000L)
                cluster.speeds.add(1000000L)
                cluster.speeds.add(900000L)
                cluster.speeds.add(800000L)
                cluster.speeds.add(700000L)
                cluster.speeds.add(600000L)
                cluster.speeds.add(500000L)
                cluster.speeds.add(400000L)
                cluster.powers.add(141f)
                cluster.powers.add(126f)
                cluster.powers.add(111f)
                cluster.powers.add(98f)
                cluster.powers.add(89f)
                cluster.powers.add(80f)
                cluster.powers.add(70f)
                cluster.powers.add(65f)
                cluster.powers.add(54f)
                cluster.powers.add(50f)
                cluster.powers.add(46f)
                cluster.powers.add(42f)
                val tmp: MutableList<CpuCoreCluster> =
                    ArrayList<CpuCoreCluster>()
                tmp.add(cluster)
                return PowerProfile(tmp)
            }

        init {
            System.loadLibrary("affinity-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PID = Process.myPid()
        mResultTextView = findViewById<View>(R.id.tv_result) as TextView
        mResultTextView!!.text = ""
        //mResultTextView.setText("PID: " + Process.myPid() + "\n");
        mResultTextView!!.append(
            """
                opt ${if (isOptimized == 0) "off" else "on"}
                
                """.trimIndent()
        )
        mResultTextView!!.append("mask = $MASK\n")
        mResultTextView!!.append("cycles = $MAX_CYCLES\n")
        mStartTestButton =
            findViewById<View>(R.id.b_startTest) as Button
        mStartTestButton!!.text = "Start $TEST_NUMBER tests"
        mStartTestButton!!.setOnClickListener {
            var avge = 0f
            executeCMDWrite("echo $MAX_FREQ > /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            var warmup: Thread
            for (i in 0..2) {
                warmup = Thread(CalcTask())
                warmup.start()
                try {
                    warmup.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            mResultTextView!!.append("W.  ec: $TOTAL_ENERGY\n")
            for (i in 1..TEST_NUMBER) {
                CURR_ATTEMPT = i
                val calc = Thread(CalcTask())
                calc.start()
                try {
                    calc.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                mResultTextView!!.append("$i.  ec: $TOTAL_ENERGY\n")
                avge += TOTAL_ENERGY
            }
            mResultTextView!!.append("Average ec: " + avge / TEST_NUMBER)
            executeCMDWrite("echo $MIN_FREQ > /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
        }
    }

    private class CalcTask : Runnable {
        override fun run() {
            var cycles = MAX_CYCLES
            setAffinity(MASK)
            val r1: MeasurementTool.Reading
            val r2: MeasurementTool.Reading

            val tmpstr = StringBuilder()
            val dummy = StringBuilder()
            var tmpint = 0

            val testnum = TEST_i

            Log.d(TAG, "run: ====================================================================")
            Log.d(TAG, "run: CURRENT TEST: $CURR_ATTEMPT")

            r1 = MeasurementTool.makeMeasurement(0x11111111)

            for (i in 0 until cycles) {
                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)

                tmpint += StatHelper.triangularNumber(10000)
                tmpint += StatHelper.fibonacci(10000)
                tmpint += StatHelper.gcd(10000, 3221)
            }

            r2 = MeasurementTool.makeMeasurement(0x11111111)

            val diff: MeasurementTool.Reading = MeasurementTool.findDiff(r1, r2)

            TOTAL_ENERGY = MeasurementTool.analyzeMeasurement(diff, PROFILE)
            Log.d(TAG,"run: elapsed ec:   $TOTAL_ENERGY")
            Log.d(TAG, "run: elapsed ec:   $diff")
            println(tmpint * tmpstr.hashCode())
        }

        external fun setAffinity(arg: Int)
    }

    private fun executeCMDWrite(cmd: String) {
        try {
            val p = Runtime.getRuntime().exec("su")
            val dos = DataOutputStream(p.outputStream)
            val `is` = DataInputStream(p.inputStream)
            dos.writeBytes("$cmd\nexit\n")
            dos.flush()
            dos.close()
            p.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}