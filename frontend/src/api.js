async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  })

  const responseText = await response.text()
  let responseBody = responseText

  if (responseText) {
    try {
      responseBody = JSON.parse(responseText)
    } catch {
      responseBody = responseText
    }
  }

  if (!response.ok) {
    const message = typeof responseBody === 'string'
      ? responseBody
      : responseBody?.message || response.statusText
    throw new Error(message || `Request failed with status ${response.status}`)
  }

  return responseBody
}

export function get(path) {
  return request(path)
}

export function send(path, method, body) {
  return request(path, {
    method,
    ...(body === undefined ? {} : { body: JSON.stringify(body) }),
  })
}
