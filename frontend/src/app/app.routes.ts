import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { GraduateDashboardComponent } from './pages/graduate-dashboard/graduate-dashboard.component';
import { MentorListComponent } from './pages/mentor-list/mentor-list.component';
import { ChatComponent } from './pages/chat/chat.component';
import { MentorDashboardComponent } from './pages/mentor-dashboard/mentor-dashboard.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { authGuard, guestOnlyGuard, roleGuard, rootRedirectGuard } from './core/services/route.guards';

export const appRoutes: Routes = [
  { path: '', canActivate: [rootRedirectGuard], component: LoginComponent },
  { path: 'login', component: LoginComponent, canActivate: [guestOnlyGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestOnlyGuard] },
  { path: 'graduate-dashboard', component: GraduateDashboardComponent, canActivate: [authGuard, roleGuard(['GRADUATE'])] },
  { path: 'mentors', component: MentorListComponent, canActivate: [authGuard, roleGuard(['GRADUATE'])] },
  { path: 'chat', component: ChatComponent, canActivate: [authGuard] },
  { path: 'mentor-dashboard', component: MentorDashboardComponent, canActivate: [authGuard, roleGuard(['MENTOR'])] },
  { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [authGuard, roleGuard(['ADMIN'])] },
  { path: '**', canActivate: [rootRedirectGuard], component: LoginComponent }
];
