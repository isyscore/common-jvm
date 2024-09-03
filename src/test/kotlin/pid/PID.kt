package pid

data class PIDController(
    /* controller gains */
    var Kp: Double = 0.0, var Ki: Double = 0.0, var Kd: Double = 0.0,
    /* Derivative low-pass filter time constant */
    var tau: Double = 0.0,
    /* Output limits */
    var limMin: Double = 0.0, var limMax: Double = 0.0,
    /* Integrator limits */
    var limMinInt: Double = 0.0, var limMaxInt: Double = 0.0,
    /* Sample time (in seconds) */
    var t: Double = 0.0,
    /* Controller memory */
    var integrator: Double = 0.0, var prevError: Double = 0.0, var differentiator: Double = 0.0, var prevMeasurement: Double = 0.0,
    /* Controller output */
    var out: Double = 0.0
)

/* Clear controller variables */
fun PIDControllerInit(pid: PIDController) {
    pid.integrator = 0.0
    pid.prevError = 0.0
    pid.differentiator = 0.0
    pid.prevMeasurement = 0.0
    pid.out = 0.0
}

fun PIDControllerUpdate(pid: PIDController, setPoint: Double, measurement: Double): Double {
    /* error signal */
    val error = setPoint - measurement
    /* Proportional */
    val proportional = pid.Kp * error
    /* Integral */
    pid.integrator += 0.5 * pid.Ki * pid.t * (pid.prevError + error)
    /* Anti-wind-up via integrator clamping */
    if (pid.integrator > pid.limMaxInt) {
        pid.integrator = pid.limMaxInt
    } else if (pid.integrator < pid.limMinInt) {
        pid.integrator = pid.limMinInt
    }
    /* Derivative (band-limited differentiator) */
    pid.differentiator = -(2.0 * pid.Kd * (measurement - pid.prevMeasurement) + (2.0 * pid.tau - pid.t) * pid.differentiator) / (2.0 * pid.tau + pid.t)
    /* Compute output and apply limits */
    pid.out = proportional + pid.integrator + pid.differentiator
    if (pid.out > pid.limMax) {
        pid.out = pid.limMax
    } else if (pid.out < pid.limMin) {
        pid.out = pid.limMin
    }
    /* Store error and measurement for later use */
    pid.prevError = error
    pid.prevMeasurement = measurement

    /* Return controller output */
    return pid.out
}