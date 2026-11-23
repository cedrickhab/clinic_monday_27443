export const resources = {
  patients: {
    title: 'Patients',
    singular: 'patient',
    endpoint: '/api/patients',
    related: [],
    fields: [
      { name: 'firstName', label: 'First name', required: true },
      { name: 'lastName', label: 'Last name', required: true },
      { name: 'dateOfBirth', label: 'Date of birth', type: 'date', required: true },
      { name: 'ssNumber', label: 'Social security number', required: true },
    ],
    columns: [
      { label: 'First name', value: (item) => item.firstName },
      { label: 'Last name', value: (item) => item.lastName },
      { label: 'Date of birth', value: (item) => item.dateOfBirth },
      { label: 'Social security number', value: (item) => item.ssNumber },
    ],
  },
  doctors: {
    title: 'Doctors',
    singular: 'doctor',
    endpoint: '/api/doctors',
    related: ['offices'],
    fields: [
      { name: 'firstName', label: 'First name', required: true },
      { name: 'lastName', label: 'Last name', required: true },
      { name: 'dateOfBirth', label: 'Date of birth', type: 'date', required: true },
      { name: 'officeId', label: 'Office', type: 'select', options: 'offices', optionLabel: (item) => `${item.name} (${item.officeNumber})` },
    ],
    columns: [
      { label: 'First name', value: (item) => item.firstName },
      { label: 'Last name', value: (item) => item.lastName },
      { label: 'Date of birth', value: (item) => item.dateOfBirth },
      { label: 'Office', value: (item) => item.office ? `${item.office.name} (${item.office.officeNumber})` : 'None' },
    ],
  },
  offices: {
    title: 'Offices',
    singular: 'office',
    endpoint: '/api/offices',
    related: [],
    fields: [
      { name: 'name', label: 'Office name', required: true },
      { name: 'officeNumber', label: 'Office number', type: 'number', required: true },
    ],
    columns: [
      { label: 'Office name', value: (item) => item.name },
      { label: 'Office number', value: (item) => item.officeNumber },
    ],
  },
  appointments: {
    title: 'Appointments',
    singular: 'appointment',
    endpoint: '/api/appointments',
    related: ['patients', 'doctors'],
    fields: [
      { name: 'appointmentDate', label: 'Appointment date', type: 'date', required: true },
      { name: 'patientId', label: 'Patient', type: 'select', options: 'patients', optionLabel: (item) => `${item.firstName} ${item.lastName}`, required: true },
      { name: 'doctorId', label: 'Doctor', type: 'select', options: 'doctors', optionLabel: (item) => `Dr. ${item.firstName} ${item.lastName}`, required: true },
      { name: 'reason', label: 'Reason', required: true },
    ],
    columns: [
      { label: 'Date', value: (item) => item.appointmentDate },
      { label: 'Patient', value: (item) => `${item.patient?.firstName || ''} ${item.patient?.lastName || ''}` },
      { label: 'Doctor', value: (item) => `Dr. ${item.doctor?.firstName || ''} ${item.doctor?.lastName || ''}` },
      { label: 'Reason', value: (item) => item.reason },
      { label: 'Status', value: (item) => item.status },
    ],
  },
  prescriptions: {
    title: 'Prescriptions',
    singular: 'prescription',
    endpoint: '/api/prescriptions',
    related: ['appointments'],
    createPath: (form) => `/api/prescriptions/save?appointmentId=${encodeURIComponent(form.appointmentId)}`,
    updatePath: (id) => `/api/prescriptions/update/${id}`,
    deletePath: (id) => `/api/prescriptions/delete/${id}`,
    fields: [
      { name: 'appointmentId', label: 'Completed appointment', type: 'select', options: 'completedAppointments', optionLabel: (item) => `${item.appointmentDate} · ${item.patient?.firstName || ''} ${item.patient?.lastName || ''}`, required: true },
      { name: 'medicineName', label: 'Medicine name', required: true },
      { name: 'dosage', label: 'Dosage', required: true },
      { name: 'durationDays', label: 'Duration (days)', type: 'number', required: true },
    ],
    editFields: [
      { name: 'medicineName', label: 'Medicine name', required: true },
      { name: 'dosage', label: 'Dosage', required: true },
      { name: 'durationDays', label: 'Duration (days)', type: 'number', required: true },
    ],
    columns: [
      { label: 'Medicine', value: (item) => item.medicineName },
      { label: 'Dosage', value: (item) => item.dosage },
      { label: 'Duration', value: (item) => `${item.durationDays} days` },
      { label: 'Issued', value: (item) => item.issuedDate },
      { label: 'Appointment date', value: (item) => item.appointment?.appointmentDate },
    ],
  },
  specializations: {
    title: 'Specializations',
    singular: 'specialization',
    endpoint: '/api/specializations',
    related: ['doctors'],
    fields: [
      { name: 'name', label: 'Specialization name', required: true },
      { name: 'doctorIds', label: 'Doctors', type: 'multiple', options: 'doctors', optionLabel: (item) => `Dr. ${item.firstName} ${item.lastName}` },
    ],
    columns: [
      { label: 'Specialization', value: (item) => item.name },
      { label: 'Doctors', value: (item) => item.doctors?.map((doctor) => `Dr. ${doctor.firstName} ${doctor.lastName}`).join(', ') || 'None' },
    ],
  },
}

export function getEmptyForm(fields) {
  const form = {}
  fields.forEach((field) => {
    form[field.name] = field.type === 'multiple' ? [] : ''
  })
  return form
}

export function getFormValues(item, fields) {
  const form = getEmptyForm(fields)
  if (!item) return form

  fields.forEach((field) => {
    if (field.name === 'officeId') form.officeId = item.office?.id || ''
    else if (field.name === 'patientId') form.patientId = item.patient?.id || ''
    else if (field.name === 'doctorId') form.doctorId = item.doctor?.id || ''
    else if (field.name === 'appointmentId') form.appointmentId = item.appointment?.id || ''
    else if (field.name === 'doctorIds') form.doctorIds = item.doctors?.map((doctor) => doctor.id) || []
    else form[field.name] = item[field.name] ?? ''
  })
  return form
}

export function getPayload(key, form) {
  const payload = { ...form }

  if (key === 'doctors') {
    payload.office = form.officeId ? { id: form.officeId } : null
    delete payload.officeId
  }

  if (key === 'appointments') {
    payload.patient = { id: form.patientId }
    payload.doctor = { id: form.doctorId }
    delete payload.patientId
    delete payload.doctorId
  }

  if (key === 'prescriptions') {
    delete payload.appointmentId
    payload.durationDays = Number(payload.durationDays)
  }

  if (key === 'specializations') {
    payload.doctors = (form.doctorIds || []).map((id) => ({ id }))
    delete payload.doctorIds
  }

  if (key === 'offices') payload.officeNumber = Number(payload.officeNumber)
  return payload
}

export function getCreatePath(resource, form) {
  return resource.createPath ? resource.createPath(form) : `${resource.endpoint}/save`
}

export function getUpdatePath(resource, id) {
  return resource.updatePath ? resource.updatePath(id) : `${resource.endpoint}/${id}`
}

export function getDeletePath(resource, id) {
  return resource.deletePath ? resource.deletePath(id) : `${resource.endpoint}/${id}`
}
