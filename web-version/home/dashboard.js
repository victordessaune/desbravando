
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
