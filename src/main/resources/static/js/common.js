
document.addEventListener('DOMContentLoaded',()=>{
	document.getElementById('signOutLink').addEventListener('click',()=>{
		document.getElementById('signoutForm').submit()
	})
	
	Array.from(document.getElementsByClassName('changeStatusLink'))
	.forEach(
		link => link.addEventListener('click',()=>{
			
			document.getElementById('changeUserId').setAttribute('value',link.getAttribute('data-id'))
			document.getElementById('changeUserStatus').setAttribute('value',link.getAttribute('data-status'))
			
			document.getElementById('changeStatusUserName').innerText = link.getAttribute('data-user')
			document.getElementById('changeStatusBefore').innerText = link.getAttribute('data-status') == 'true' ? 'Active' : 'Suspend'
			document.getElementById('changeStatusAfter').innerText  = link.getAttribute('data-status') == 'true' ? 'Suspend' : 'Active'
			
			var dialog = new bootstrap.Modal("#changeStatus");
			dialog.show();
		})
	)
	
	var sizeChangeSelectForm = document.getElementById('sizeChangeSelectForm');
	
	if(sizeChangeSelectForm){
		sizeChangeSelectForm.addEventListener('change',()=>{
			var accessLogSearchForm = document.getElementById('accessLogSearchForm')
			var size = sizeChangeSelectForm.value
			
			var sizeInput = document.createElement('input')
			sizeInput.setAttribute('type','hidden')
			sizeInput.setAttribute('name','size')
			sizeInput.setAttribute('value',size)
			accessLogSearchForm.appendChild(sizeInput)
			accessLogSearchForm.submit()
			
		})
	}
})