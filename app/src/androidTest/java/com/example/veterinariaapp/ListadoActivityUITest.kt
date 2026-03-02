package com.example.veterinariaapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
class ListadoActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ListadoActivityV4::class.java)

    @Test
    fun tabsNavigation_cambiaEntreMascotasYConsultas() {
        onView(withText("🐾 Mascotas")).check(matches(isSelected()))
        onView(withId(R.id.recyclerViewMascotas)).check(matches(isDisplayed()))
        onView(withText("📋 Consultas")).perform(click())
        onView(withId(R.id.tvEmptyConsultas)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}