export default {
  mounted(el, binding) {
    const permission = binding.value
    
    if (permission) {
      const permissions = JSON.parse(localStorage.getItem('permissions') || '[]')
      const hasPermission = permissions.includes(permission)
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}
