import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import UploadPage from './pages/UploadPage'
import ScanPage from './pages/ScanPage'
import ReportPage from './pages/ReportPage'
import AllScansPage from './pages/AllScansPage'
import './index.css'

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/"              element={<UploadPage />} />
        <Route path="/scans"         element={<AllScansPage />} />
        <Route path="/scan/:scanId"  element={<ScanPage />} />
        <Route path="/report/:scanId" element={<ReportPage />} />
      </Routes>
    </BrowserRouter>
  )
}
