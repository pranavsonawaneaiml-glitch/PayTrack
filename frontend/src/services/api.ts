import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Mock data for demonstration when backend is not running
const MOCK_DATA = {
  employees: [
    {
      id: 1,
      firstName: "John",
      lastName: "Doe",
      email: "john.doe@company.com",
      phone: "+1-555-0123",
      department: "Engineering",
      position: "Software Engineer",
      salary: 75000.00,
      hireDate: "2024-01-15",
      terminationDate: null,
      isActive: true
    },
    {
      id: 2,
      firstName: "Jane",
      lastName: "Smith",
      email: "jane.smith@company.com",
      phone: "+1-555-0124",
      department: "Marketing",
      position: "Marketing Manager",
      salary: 65000.00,
      hireDate: "2024-02-01",
      terminationDate: null,
      isActive: true
    }
  ],
  salaries: [
    {
      id: 1,
      employee: { id: 1, firstName: "John", lastName: "Doe" },
      payPeriodStart: "2024-01-01",
      payPeriodEnd: "2024-01-31",
      baseSalary: 6250.00,
      overtimePay: 500.00,
      bonus: 1000.00,
      deductions: 250.00,
      netPay: 7500.00,
      paymentDate: "2024-02-01",
      paymentStatus: "PAID",
      notes: "Monthly salary payment"
    }
  ]
};

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add response interceptor to handle backend not running
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.warn('Backend not available, using mock data');
    // Return mock data when backend is not running
    if (error.code === 'ERR_NETWORK' || error.response?.status === 404) {
      const url = error.config.url;
      const method = error.config.method;

      if (method === 'get' && url?.includes('/employees')) {
        if (url.includes('/active')) {
          return Promise.resolve({ data: MOCK_DATA.employees.filter(e => e.isActive) });
        } else if (url.includes('/inactive')) {
          return Promise.resolve({ data: MOCK_DATA.employees.filter(e => !e.isActive) });
        } else if (url.includes('/stats/active')) {
          return Promise.resolve({ data: MOCK_DATA.employees.filter(e => e.isActive).length });
        } else if (url.includes('/stats/total')) {
          return Promise.resolve({ data: MOCK_DATA.employees.length });
        } else if (url.includes('/search?name=')) {
          const searchTerm = url.split('name=')[1];
          const filtered = MOCK_DATA.employees.filter(e =>
            `${e.firstName} ${e.lastName}`.toLowerCase().includes(searchTerm.toLowerCase())
          );
          return Promise.resolve({ data: filtered });
        } else if (url.match(/\/\d+$/)) {
          const id = parseInt(url.split('/').pop() || '0');
          const employee = MOCK_DATA.employees.find(e => e.id === id);
          return employee ? Promise.resolve({ data: employee }) : Promise.reject(error);
        }
        return Promise.resolve({ data: MOCK_DATA.employees });
      }

      if (method === 'get' && url?.includes('/salaries')) {
        if (url.includes('/pending')) {
          return Promise.resolve({ data: MOCK_DATA.salaries.filter(s => s.paymentStatus === 'PENDING') });
        } else if (url.includes('/payroll/total')) {
          const total = MOCK_DATA.salaries.reduce((sum, s) => sum + s.netPay, 0);
          return Promise.resolve({ data: total });
        } else if (url.match(/\/\d+$/)) {
          const id = parseInt(url.split('/').pop() || '0');
          const salary = MOCK_DATA.salaries.find(s => s.id === id);
          return salary ? Promise.resolve({ data: salary }) : Promise.reject(error);
        }
        return Promise.resolve({ data: MOCK_DATA.salaries });
      }
    }

    return Promise.reject(error);
  }
);

// Employee API calls
export const employeeAPI = {
  getAll: () => api.get('/employees'),
  getActive: () => api.get('/employees/active'),
  getById: (id: number) => api.get(`/employees/${id}`),
  getByEmail: (email: string) => api.get(`/employees/email/${email}`),
  getByDepartment: (department: string) => api.get(`/employees/department/${department}`),
  searchByName: (name: string) => api.get(`/employees/search?name=${name}`),
  create: (employee: any) => api.post('/employees', employee),
  update: (id: number, employee: any) => api.put(`/employees/${id}`, employee),
  deactivate: (id: number) => api.patch(`/employees/${id}/deactivate`),
  activate: (id: number) => api.patch(`/employees/${id}/activate`),
  delete: (id: number) => api.delete(`/employees/${id}`),
  getActiveCount: () => api.get('/employees/stats/active'),
  getTotalCount: () => api.get('/employees/stats/total'),
};

// Salary API calls
export const salaryAPI = {
  getAll: () => api.get('/salaries'),
  getByEmployee: (employeeId: number) => api.get(`/salaries/employee/${employeeId}`),
  getRecentByEmployee: (employeeId: number) => api.get(`/salaries/employee/${employeeId}/recent`),
  getById: (id: number) => api.get(`/salaries/${id}`),
  getByDateRange: (startDate: string, endDate: string) =>
    api.get(`/salaries/date-range?startDate=${startDate}&endDate=${endDate}`),
  getByEmployeeAndDateRange: (employeeId: number, startDate: string, endDate: string) =>
    api.get(`/salaries/employee/${employeeId}/date-range?startDate=${startDate}&endDate=${endDate}`),
  create: (salary: any) => api.post('/salaries', salary),
  update: (id: number, salary: any) => api.put(`/salaries/${id}`, salary),
  delete: (id: number) => api.delete(`/salaries/${id}`),
  getTotalPayroll: (startDate: string, endDate: string) =>
    api.get(`/salaries/payroll/total?startDate=${startDate}&endDate=${endDate}`),
  getAverageSalary: (employeeId: number) => api.get(`/salaries/employee/${employeeId}/average`),
  getPendingPayments: () => api.get('/salaries/pending'),
  markAsPaid: (id: number) => api.patch(`/salaries/${id}/mark-paid`),
  markAsProcessed: (id: number) => api.patch(`/salaries/${id}/mark-processed`),
};

// Auth API calls
export const authAPI = {
  login: (email: string, password: string) => api.post('/auth/login', { email, password }),
  register: (data: any) => api.post('/auth/register', data),
};

// Payroll API calls
export const payrollAPI = {
  generate: (year: number, month: number) => api.post(`/payroll/generate/${year}/${month}`),
  checkStatus: (year: number, month: number) => api.get(`/payroll/status/${year}/${month}`),
};

// Export API calls
export const exportAPI = {
  downloadPayslip: (salaryId: number) => api.get(`/export/payslip/${salaryId}/pdf`, { responseType: 'blob' }),
  exportPayrollCsv: (year: number, month: number) => api.get(`/export/payroll/${year}/${month}/csv`, { responseType: 'blob' }),
};

export default api;