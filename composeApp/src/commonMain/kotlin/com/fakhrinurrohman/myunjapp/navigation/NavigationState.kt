package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute by topLevelRoute

    val stackInUse: List<NavKey>
        get() = if(topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = serializerConfig,
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class)),
    ) {
        mutableStateOf(startRoute)
    }
    val backStacks = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = serializerConfig,
            key
        )
    }

    return remember(startRoute, topLevelRoute) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

val serializerConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            // All the routes in the app
            subclass(Route.HomePage::class, Route.HomePage.serializer())
            subclass(Route.TasksPage::class, Route.TasksPage.serializer())
            subclass(Route.SchedulePage::class, Route.SchedulePage.serializer())
            subclass(Route.InformationPage::class, Route.InformationPage.serializer())
            subclass(Route.MorePage::class, Route.MorePage.serializer())

            subclass(Route.Login::class, Route.Login.serializer())

            subclass(Route.SettingScreenChange::class, Route.SettingScreenChange.serializer())

            subclass(Route.ManageSemester::class, Route.ManageSemester.serializer())
            subclass(Route.AddSemester::class, Route.AddSemester.serializer())
            subclass(Route.EditSemester::class, Route.EditSemester.serializer())

            subclass(Route.ManageCourse::class, Route.ManageCourse.serializer())
            subclass(Route.AddCourse::class, Route.AddCourse.serializer())
            subclass(Route.EditCourse::class, Route.EditCourse.serializer())
            subclass(Route.DetailCourse::class, Route.DetailCourse.serializer())

            subclass(Route.AddUserEvent::class, Route.AddUserEvent.serializer())
            subclass(Route.EditUserEvent::class, Route.EditUserEvent.serializer())
            subclass(Route.DetailUserEvent::class, Route.DetailUserEvent.serializer())

            subclass(Route.WebView::class, Route.WebView.serializer())
            
            // Information Routes
            subclass(Route.NewsList::class, Route.NewsList.serializer())
            subclass(Route.CampusList::class, Route.CampusList.serializer())
            subclass(Route.BuildingList::class, Route.BuildingList.serializer())
            subclass(Route.RoomList::class, Route.RoomList.serializer())
            subclass(Route.FacultyList::class, Route.FacultyList.serializer())
            subclass(Route.StudyProgramList::class, Route.StudyProgramList.serializer())
            subclass(Route.LecturerList::class, Route.LecturerList.serializer())
            subclass(Route.LecturerDetail::class, Route.LecturerDetail.serializer())
        }
    }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }
    return stackInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
