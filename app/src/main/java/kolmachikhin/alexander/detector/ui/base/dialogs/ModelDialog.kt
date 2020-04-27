package kolmachikhin.alexander.detector.ui.base.dialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kolmachikhin.alexander.bottomappbar.ui.base.dialogs.Action
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.Model
import kolmachikhin.alexander.detector.ui.extentions.hide
import kolmachikhin.alexander.detector.ui.extentions.hideViews
import kolmachikhin.alexander.detector.ui.extentions.show
import kolmachikhin.alexander.detector.ui.extentions.showViews
import kotlinx.android.synthetic.main.model_dialog.*

abstract class ModelDialog<M: Model>(var model: M) : DialogFragment() {

    abstract val innerLayout: Int
    lateinit var innerView: View

    var mode = Mode.VIEW
    var onActionListener: ((action: Action, model: M) -> Unit)? = null

    fun onAction(l: ((action: Action, model: M) -> Unit)?) {
        onActionListener = l
    }

    val isModeView get() = mode == Mode.VIEW
    val isModeEdit get() = mode == Mode.EDIT

    var dismissAfterSave = false
    var deleteEnabled = true
    var editEnabled = true
    var cancelEnabled = true

    fun setForCreate() {
        deleteEnabled = false
        cancelEnabled = false
        dismissAfterSave = true
    }

    var isReady = false
    val onReadyListeners: ArrayList<() -> Unit> = ArrayList()

    fun onReady(l: () -> Unit) {
        if (isReady) l()
        else onReadyListeners.add(l)
    }

    fun ready() {
        isReady = true
        onReadyListeners.forEach { it() }
        onReadyListeners.clear()
    }

    var isImmutable = false

    fun setAsImmutable() {
        isImmutable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.model_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true

        buttonDelete.setOnClickListener { delete() }
        buttonCancel.setOnClickListener { cancel() }
        buttonEdit.setOnClickListener { edit() }
        buttonSave.setOnClickListener { save() }

        activity?.let {
            innerView = createInnerView(it)
            innerContainer.removeAllViews()
            innerContainer.addView(innerView)
        } ?: dismiss()
    }

    override fun onStart() {
        super.onStart()
        if (isImmutable) {
            hideViews(buttonCancel, buttonDelete, buttonSave, buttonEdit)
            setModeView()
        } else {
            showViews(buttonCancel, buttonDelete, buttonSave, buttonEdit)
        }
        setViewsByModel()
        ready()
    }

    fun createInnerView(activity: Activity) = LayoutInflater.from(activity).inflate(innerLayout, innerContainer, false)

    abstract fun setViewsByModel()
    abstract fun createModelByViews(oldModel: M): M

    abstract fun onModeView()
    abstract fun onModeEdit()

    open fun setModeView() {
        mode = Mode.VIEW
        onReady {
            if (!isImmutable) {
                buttonEdit.run { if (editEnabled) show() else hide() }
                buttonDelete.run { if (deleteEnabled) show() else hide() }
                hideViews(buttonCancel, buttonSave)
            }
            onModeView()
            setViewsByModel()
        }
    }

    open fun setModeEdit() {
        if (!isImmutable) {
            mode = Mode.EDIT
            onReady {
                buttonCancel.run { if (cancelEnabled) show() else hide() }
                buttonSave.show()
                hideViews(buttonEdit, buttonDelete)
                onModeEdit()
            }
        }
    }

    fun edit() {
        setModeEdit()
        onActionListener?.invoke(Action.EDIT, model)
    }

    fun save() {
        model = createModelByViews(model)
        onActionListener?.invoke(Action.SAVE, model)
        setModeView()
        if (dismissAfterSave)
            dismiss()
    }

    fun cancel() {
        setModeView()
        onActionListener?.invoke(Action.CANCEL, model)
    }

    fun delete() {
        onActionListener?.invoke(Action.DELETE, model)
        dismiss()
    }

}