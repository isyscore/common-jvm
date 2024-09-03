package pid

import com.isyscore.kotlin.common.pi
import org.junit.Test

class PIDTest {

    companion object {
        const val PID_KP = 2.0
        const val PID_KI = 0.5
        const val PID_KD = 0.25
        const val PID_TAU = 0.02
        const val PID_LIM_MIN = -10.0
        const val PID_LIM_MAX = 10.0
        const val PID_LIM_MIN_INT = -5.0
        const val PID_LIM_MAX_INT = 5.0
        const val SAMPLE_TIME_S = 0.01
        const val SIMULATION_TIME_MAX = 4.0
    }

    private var updateOutput = 0.0
    private val updateAlpha = 0.02

    fun systemUpdate(inp: Double): Double {
        updateOutput = (SAMPLE_TIME_S * inp + updateOutput) / (1.0 + updateAlpha * SAMPLE_TIME_S)
        return updateOutput
    }

    @Test
    fun test() {

        /* Initialise PID controller */
        val pid = PIDController(
            Kp = PID_KP,
            Ki = PID_KI,
            Kd = PID_KD,
            tau = PID_TAU,
            limMin = PID_LIM_MIN,
            limMax = PID_LIM_MAX,
            limMinInt = PID_LIM_MIN_INT,
            limMaxInt = PID_LIM_MAX_INT,
            t = SAMPLE_TIME_S)
        PIDControllerInit(pid)

        /* Simulate response using test system */
        val setPoint = 1.0

        println("Time (s)\tSystem Output\tControllerOutput")
        var t = 0.0
        while (t <= SIMULATION_TIME_MAX) {
            /* Get measurement from system */
            val measurement = systemUpdate(pid.out)
            /* Compute new control signal */
            PIDControllerUpdate(pid, setPoint, measurement)
            println("%f\t%f\t%f".format(t, measurement, pid.out))
            t += SAMPLE_TIME_S
        }
    }


}