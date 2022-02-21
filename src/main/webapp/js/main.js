const $main = $('main');

$.ajax({
	url: '/api/modules',
	type: 'GET',
	headers: { 'Accept': 'application/json' }
})
	.then(modules => renderModules(modules))
	.catch(error => renderError(error));

function renderModules(modules) {
	$main.empty().append($('<h2>').text('Modules'));
	let $table = $('<table>');
	modules.forEach(module => {
		let $row = $('<tr>')
			.append($('<td>').text(module.nr))
			.append($('<td>').text(module.name))
			.append($('<td>').text(module.description));
		$table.append($row);
	});
	$main.append($table);
}

function renderError(error) {
	$main.empty().append($('<p>').addClass('error').text('Loading of modules failed!'));
}
