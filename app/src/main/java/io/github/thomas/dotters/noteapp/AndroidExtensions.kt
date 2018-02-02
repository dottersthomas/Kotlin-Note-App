package io.github.thomas.dotters.noteapp

/**
 * Created by Thomas on 07/01/2018.
 */
import android.support.annotation.AnimRes
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Method to replace the fragment. The [fragment] is added to the container view with id
 * [containerViewId] and a [tag]. The operation is performed by the supportFragmentManager.
 */
fun AppCompatActivity.replaceFragmentSafely(fragment: Fragment,
                                            tag: String,
                                            @IdRes containerViewId: Int,
                                            addToStack: Boolean = false,
                                            @AnimRes enterAnimation: Int = 0,
                                            @AnimRes exitAnimation: Int = 0,
                                            @AnimRes popEnterAnimation: Int = 0,
                                            @AnimRes popExitAnimation: Int = 0) {

    if(addToStack) {
        val ft = supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
                .addToBackStack(tag)
                .replace(containerViewId, fragment, tag)

        ft.commit()
    }else{
        val ft = supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
                .replace(containerViewId, fragment, tag)

        ft.commit()
    }

}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}