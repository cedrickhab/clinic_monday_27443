import { useEffect, useState } from 'react'
import { Link, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import { get, send } from './api.js'
import {
  getCreatePath,
  getDeletePath,
  getEmptyForm,
  getFormValues,
  getPayload,
  getUpdatePath,
  resources,
} from './resources.js'

const resourceNames = Object.keys(resources)

function App() {
  const location = useLocation()

  return (
    <div className="app">
      <header className="site-header">
        <h1>Clinic Management</h1>
        <nav>
          {resourceNames.map((name) => (
            <Link className={location.pathname === `/${name}` ? 'active-link' : ''} key={name} to={`/${name}`}>
              {resources[name].title}
            </Link>
          ))}
        </nav>
      </header>

      <main>
        <Routes>
          <Route path="/" element={<Navigate to="/offices" replace />} />
          <Route path="/patients" element={<CrudPage resourceName="patients" />} />
          <Route path="/doctors" element={<CrudPage resourceName="doctors" />} />
          <Route path="/offices" element={<CrudPage resourceName="offices" />} />
          <Route path="/appointments" element={<CrudPage resourceName="appointments" />} />
          <Route path="/prescriptions" element={<CrudPage resourceName="prescriptions" />} />
          <Route path="/specializations" element={<CrudPage resourceName="specializations" />} />
        </Routes>
      </main>
    </div>
  )
}

function CrudPage({ resourceName }) {
  const resource = resources[resourceName]
  const [records, setRecords] = useState([])
  const [relatedRecords, setRelatedRecords] = useState({})
  const [form, setForm] = useState(getEmptyForm(resource.fields))
  const [editingId, setEditingId] = useState(null)
  const [search, setSearch] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)

  async function loadData() {
    setLoading(true)
    try {
      const items = await get(`${resource.endpoint}/all`)
      const related = {}
      await Promise.all(resource.related.map(async (name) => {
        related[name] = await get(`${resources[name].endpoint}/all`)
      }))
      setRecords(items)
      setRelatedRecords(related)
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    setForm(getEmptyForm(resource.fields))
    setEditingId(null)
    setSearch('')
    setMessage('')
    loadData()
  }, [resourceName])

  function changeField(name, value) {
    setForm((currentForm) => ({ ...currentForm, [name]: value }))
  }

  function editRecord(record) {
    const fields = resource.editFields || resource.fields
    setForm(getFormValues(record, fields))
    setEditingId(record.id)
    setMessage('')
  }

  function clearForm() {
    setForm(getEmptyForm(resource.fields))
    setEditingId(null)
  }

  async function saveRecord(event) {
    event.preventDefault()
    const path = editingId
      ? getUpdatePath(resource, editingId)
      : getCreatePath(resource, form)

    try {
      await send(path, editingId ? 'PUT' : 'POST', getPayload(resourceName, form))
      setMessage(`${resource.singular} saved successfully.`)
      clearForm()
      await loadData()
    } catch (error) {
      setMessage(error.message)
    }
  }

  async function deleteRecord(id) {
    if (!window.confirm(`Delete this ${resource.singular}?`)) return

    try {
      await send(getDeletePath(resource, id), 'DELETE')
      setMessage(`${resource.singular} deleted successfully.`)
      await loadData()
    } catch (error) {
      setMessage(error.message)
    }
  }

  async function changeAppointmentStatus(id, status) {
    try {
      await send(`/api/appointments/${id}/status?status=${status}`, 'PATCH')
      setMessage(`Appointment ${status.toLowerCase()}.`)
      await loadData()
    } catch (error) {
      setMessage(error.message)
    }
  }

  const fields = editingId ? resource.editFields || resource.fields : resource.fields
  const visibleRecords = records.filter((record) => resource.columns
    .map((column) => column.value(record) || '')
    .join(' ')
    .toLowerCase()
    .includes(search.toLowerCase()))

  return (
    <section className="crud-page">
      <h2>{editingId ? `Edit ${resource.singular}` : `Create ${resource.singular}`}</h2>
      <form className="record-form" onSubmit={saveRecord}>
        {fields.map((field) => (
          <label className="form-field" key={field.name}>
            <span>{field.label}</span>
            {field.type === 'select' || field.type === 'multiple' ? (
              <select
                multiple={field.type === 'multiple'}
                required={field.required}
                value={form[field.name] || (field.type === 'multiple' ? [] : '')}
                onChange={(event) => changeField(field.name, field.type === 'multiple'
                  ? Array.from(event.target.selectedOptions, (option) => option.value)
                  : event.target.value)}
              >
                {field.type !== 'multiple' && <option value="">Choose {field.label.toLowerCase()}</option>}
                {getOptions(field, relatedRecords).map((item) => (
                  <option value={item.id} key={item.id}>{field.optionLabel(item)}</option>
                ))}
              </select>
            ) : (
              <input
                type={field.type || 'text'}
                required={field.required}
                min={field.type === 'number' ? '1' : undefined}
                value={form[field.name] ?? ''}
                onChange={(event) => changeField(field.name, event.target.value)}
              />
            )}
          </label>
        ))}
        <div className="form-buttons">
          <button type="submit">{editingId ? 'Update' : 'Create'}</button>
          {editingId && <button className="cancel-button" type="button" onClick={clearForm}>Cancel</button>}
        </div>
      </form>

      <h2 className="list-heading">All {resource.title}</h2>
      <div className="list-tools">
        <input aria-label={`Search ${resource.title}`} placeholder={`Search ${resource.title.toLowerCase()}`} value={search} onChange={(event) => setSearch(event.target.value)} />
      </div>
      {message && <p className="message" role="status">{message}</p>}
      {loading ? <p>Loading...</p> : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                {resource.columns.map((column) => <th key={column.label}>{column.label}</th>)}
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {visibleRecords.map((record) => (
                <tr key={record.id}>
                  {resource.columns.map((column) => <td key={column.label}>{column.value(record) || '—'}</td>)}
                  <td className="actions">
                    {resourceName === 'appointments' && record.status === 'SCHEDULED' && (
                      <button type="button" onClick={() => changeAppointmentStatus(record.id, 'CONFIRMED')}>Confirm</button>
                    )}
                    {resourceName === 'appointments' && record.status === 'CONFIRMED' && (
                      <button type="button" onClick={() => changeAppointmentStatus(record.id, 'COMPLETED')}>Complete</button>
                    )}
                    {resourceName === 'appointments' && ['SCHEDULED', 'CONFIRMED'].includes(record.status) && (
                      <button type="button" onClick={() => changeAppointmentStatus(record.id, 'CANCELLED')}>Cancel</button>
                    )}
                    <button className="edit-button" type="button" onClick={() => editRecord(record)}>Edit</button>
                    <button className="delete-button" type="button" onClick={() => deleteRecord(record.id)}>Delete</button>
                  </td>
                </tr>
              ))}
              {visibleRecords.length === 0 && <tr><td colSpan={resource.columns.length + 1}>No {resource.title.toLowerCase()} found.</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

function getOptions(field, relatedRecords) {
  if (field.options === 'completedAppointments') {
    return (relatedRecords.appointments || []).filter((item) => item.status === 'COMPLETED')
  }
  return relatedRecords[field.options] || []
}

export default App
