package com.zs.mydog.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.zs.mydog.R
import com.zs.mydog.data.Direction
import com.zs.mydog.data.Dog
import com.zs.mydog.data.DogAction
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {

    private val viewModel = DogViewModel()

    private var directionView: TextView? = null
    private var statusView: TextView? = null
    private var timeView: TextView? = null

    private var timer: Timer? = null
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    override fun onResume() {
        super.onResume()
        resumeTimer()
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    private fun init() {
        directionView = findViewById(R.id.direction)
        statusView = findViewById(R.id.dogStatus)
        timeView = findViewById(R.id.actionTime)

        viewModel.dog.currentDirection.observe(this, Observer {
            showDirection(it)
        })

        viewModel.dog.currentAction.observe(this, Observer {
            showStatus(it.type)
        })

        viewModel.dog.actionDelay.observe(this, Observer {
            timeView?.text = "${it / 1000}"
        })

        viewModel.debugViewVisible.observe(this, Observer {
            if (it) {
                debugView.visibility = View.VISIBLE
            } else {
                debugView.visibility = View.GONE
            }
        })

        come.setOnClickListener { viewModel.onClickDirection(Direction.Type.COME) }
        goAway.setOnClickListener { viewModel.onClickDirection(Direction.Type.GO) }
        sit.setOnClickListener { viewModel.onClickDirection(Direction.Type.SIT) }
        down.setOnClickListener { viewModel.onClickDirection(Direction.Type.DOWN) }
        roll.setOnClickListener { viewModel.onClickDirection(Direction.Type.ROLL) }
        bang.setOnClickListener { viewModel.onClickDirection(Direction.Type.BANG) }

        reward.setOnClickListener { viewModel.onClickReward() }

        statusView?.setOnClickListener {
            clickCount = ++clickCount % 3

            if (clickCount % 3 == 0) {
                viewModel.toggleDebugMode()
            }
        }
    }

    private fun showDirection(direction: Direction?) {
        direction?.also { direction ->
            directionView?.apply {
                text = when (direction.type) {
                    Direction.Type.BANG -> "빵!"
                    Direction.Type.GO -> "저리가!"
                    Direction.Type.DOWN -> "엎드려!"
                    Direction.Type.SIT -> "앉아!"
                    Direction.Type.COME -> "이리와!"
                    Direction.Type.ROLL -> "굴러!"
                }
                visibility = View.VISIBLE
                scaleX = 1.0f
                scaleY = 1.0f
                animate().scaleX(0.0f).scaleY(0.0f).setDuration(Dog.DIRECTION_TIME).start()
            }
        }
    }

    private fun showStatus(actionType: DogAction.Type) {
        statusView?.text = when (actionType) {
            DogAction.Type.COME -> "강아지가 가까이 다가옵니다."
            DogAction.Type.GO_AWAY -> "강아지가 멀리 떠납니다."
            DogAction.Type.BANG -> "죽은척 합니다."
            DogAction.Type.SIT -> "강아지가 앉았습니다."
            DogAction.Type.DOWN -> "강아지가 엎드렸습니다."
            DogAction.Type.ROLL -> "강아지가 구릅니다."
            DogAction.Type.EAT -> "맛있게 간식을 먹고 있습니다."
            else -> "가만히 있습니다."
        }
    }

    private fun resumeTimer() {
        val period = 1000L
        timer = fixedRateTimer("timer", false, 0L, period) {
            this@MainActivity.runOnUiThread {
                viewModel.updateTime(period)
                updateDebugView()
            }
        }
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun updateDebugView() {
        debugCome.text = "${viewModel.dog.findDogAction(DogAction.Type.COME)?.weight ?: 0}"
        debugDown.text = "${viewModel.dog.findDogAction(DogAction.Type.DOWN)?.weight ?: 0}"
        debugGo.text = "${viewModel.dog.findDogAction(DogAction.Type.GO_AWAY)?.weight ?: 0}"
        debugLie.text = "${viewModel.dog.findDogAction(DogAction.Type.BANG)?.weight ?: 0}"
        debugRoll.text = "${viewModel.dog.findDogAction(DogAction.Type.ROLL)?.weight ?: 0}"
        debugSit.text = "${viewModel.dog.findDogAction(DogAction.Type.SIT)?.weight ?: 0}"

        /*if (viewModel.dog.currentDirection.value == null) {
            debugDirectionName.text = "없음"
            debugDirectionCome.text = ""
            debugDirectionDown.text = ""
            debugDirectionGo.text = ""
            debugDirectionLie.text = ""
            debugDirectionRoll.text = ""
            debugDirectionSit.text = ""
        } else */

        viewModel.dog.currentDirection.value?.apply {
            debugDirectionName.text = viewModel.dog.currentDirection.value?.type?.toString()
            debugDirectionCome.text = "${actionWeights[DogAction.Type.COME] ?: 0}"
            debugDirectionDown.text = "${actionWeights[DogAction.Type.DOWN] ?: 0}"
            debugDirectionGo.text = "${actionWeights[DogAction.Type.GO_AWAY] ?: 0}"
            debugDirectionLie.text = "${actionWeights[DogAction.Type.BANG] ?: 0}"
            debugDirectionRoll.text = "${actionWeights[DogAction.Type.ROLL] ?: 0}"
            debugDirectionSit.text = "${actionWeights[DogAction.Type.SIT] ?: 0}"
        }
    }
}
