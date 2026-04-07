
  function atualizarData() {
    const data = new Date();

    const dias = ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"];
    const meses = ["janeiro", "fevereiro", "março", "abril", "maio", "junho",
                   "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"];

    const diaSemana = dias[data.getDay()];
    const dia = data.getDate();
    const mes = meses[data.getMonth()];
    const ano = data.getFullYear();

    const texto = `${diaSemana}, ${dia} de ${mes} de ${ano}`;

    document.getElementById("data-atual").innerText = texto;
  }

  atualizarData();
new Chart(document.getElementById('donutChart'), {
  type: 'doughnut',
  data: {
    datasets: [{
      data: [49, 32, 28, 19],
      backgroundColor: ['#7F77DD', '#1D9E75', '#D4537E', '#EF9F27'],
      borderWidth: 0
    }]
  },
  options: {
    cutout: '72%',
    plugins: { legend: { display: false }, tooltip: { enabled: false } }
  }
});