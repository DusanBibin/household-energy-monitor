import { Component } from '@angular/core';

@Component({
  selector: 'app-proba',
  standalone: false,
  templateUrl: './proba.component.html',
  styleUrl: './proba.component.css'
})
export class ProbaComponent {
  chartOption: any;

  ngOnInit(): void {
    this.chartOption = {
      title: {
        text: 'Example Bar Chart'
      },
      tooltip: {},
      legend: {
        data: ['Sales']
      },
      xAxis: {
        data: ['Shirts', 'Cardigans', 'Chiffons', 'Pants', 'Heels', 'Socks']
      },
      yAxis: {},
      series: [{
        name: 'Sales',
        type: 'bar',
        data: [5, 20, 36, 10, 10, 20]
      }]
    };
  }

  // Makes chart responsive
  onChartInit(ec: any) {
    window.addEventListener('resize', () => {
      ec.resize();
    });
  }
}
