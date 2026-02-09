import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { GraduateDashboardComponent } from './pages/graduate-dashboard/graduate-dashboard.component';
import { MentorListComponent } from './pages/mentor-list/mentor-list.component';
import { ChatComponent } from './pages/chat/chat.component';
import { MentorDashboardComponent } from './pages/mentor-dashboard/mentor-dashboard.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';

export const appRoutes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'graduate-dashboard', component: GraduateDashboardComponent },
  { path: 'mentors', component: MentorListComponent },
  { path: 'chat', component: ChatComponent },
  { path: 'mentor-dashboard', component: MentorDashboardComponent },
  { path: 'admin-dashboard', component: AdminDashboardComponent }
];
