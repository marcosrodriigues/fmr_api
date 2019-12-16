package mr.fmr.controller;

import mr.fmr.model.*;
import mr.fmr.payload.RepublicaPorPersonalidadePayload;
import mr.fmr.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
@RolesAllowed("")
public class AdminController {

    @Autowired
    private RepublicaService repService;
    @Autowired private EstudanteService estudanteService;
    @Autowired private UserService userService;
    @Autowired private RecomendacaoService recomendacaoService;
    @Autowired private PersonalidadeService personalidadeService;

    @GetMapping(value = "/admin/state")
    public StatePayload state() {
        StatePayload sp = new StatePayload();

        sp.cadastros = userService.findAll().size();
        sp.republicas = repService.findAll().size();
        sp.estudantes = estudanteService.findAll().size();
        sp.estudantes_aprovados = estudanteService.findMoradorRepublicIsAprovado().size();
        sp.estudantes_pendentes = estudanteService.findMoradorRepublicIsPendente().size();
        sp.estudantes_sem_rep = estudanteService.findMoradorRepublicIsNull().size();

        return sp;
    }

    @GetMapping(value = "/admin/republicas")
    public List<AdminRepublica> allRepublicas() {
        List<AdminRepublica> republicas = new ArrayList<>();
        List<Republica> all = repService.findAll();
        for (Republica r : all)
            republicas.add(new AdminRepublica(r));

        return republicas;
    }

    @GetMapping(value = "/admin/estudantesSemRepublica")
    public List<AdminEstudante> allDstudantes() {
        List<AdminEstudante> estudantes = new ArrayList<>();
        List<Estudante> all = estudanteService.findMoradorRepublicIsNull();
        for (Estudante e : all)
            estudantes.add(new AdminEstudante(e));

        return estudantes;


    }

    @PostMapping(value = "/admin/republica/setPendente/{id}")
    public AdminRepublica setPendente(@PathVariable("id") Long id) {
        Estudante estudante = estudanteService.findOne(id);
        estudante.getMoradorRepublica().setAprovado(false);
        estudante = estudanteService.save(estudante);

        return new AdminRepublica(estudante.getMoradorRepublica().getRepublica());
    }

    @PostMapping(value = "/admin/republica/setAprovado/{id}")
    public AdminRepublica setAprovado(@PathVariable("id") Long id) {
        Estudante estudante = estudanteService.findOne(id);
        estudante.getMoradorRepublica().setAprovado(true);
        estudante = estudanteService.save(estudante);

        return new AdminRepublica(estudante.getMoradorRepublica().getRepublica());
    }

    @PostMapping(value = "/admin/republica/updatePersonalidade/{id}")
    public AdminRepublica updatePersonalidade(@PathVariable("id") Long id) {
        Republica republica = repService.findOne(id);
        republica = repService.createPersonality(republica);
        return new AdminRepublica(republica);
    }

    @GetMapping(value = "/admin/recomendar/{id}")
    public List<RepublicaPorPersonalidadePayload> recomendar(@PathVariable("id") Long userId) {
        User me = userService.findOne(userId);

        List<Republica> republicas = repService.findAll();
        List<RepublicaPorPersonalidadePayload> retorno = new ArrayList<>();
        RepublicaPorPersonalidadePayload payload;
        int sumPersonality = 0, distanciaGeral = 0;
        for (Republica r : republicas) {
                payload = new RepublicaPorPersonalidadePayload(r);

                if (r.getPerfil().getPersonalidade() == null || me.getPerfil().getPersonalidade() == null) {
                    sumPersonality = 0;
                    distanciaGeral = 99999;
                } else {
                    sumPersonality = recomendacaoService.somaPersonalidade(r.getPerfil().getPersonalidade());
                    distanciaGeral = recomendacaoService.calculaDistanciaGeral(me, r);
                }

                payload.setDistanciaGeral(distanciaGeral);
                payload.setSomaPersonalidade(sumPersonality);

                retorno.add(payload);
        }
        return recomendacaoService.ordenar(retorno);
    }

    class AdminEstudante {
        Long id;
        String apelido;
        String nome;
        Personalidade personalidade;
        PersonalidadeNormalizada personalidadeNormalizada;

        public AdminEstudante(Estudante e) {
            this.id = e.getId();
            this.nome = e.getNome();
            this.apelido = e.getApelido();

            if (e.getPerfil().getPersonalidade() != null) {
                this.personalidade = e.getPerfil().getPersonalidade();
                this.personalidadeNormalizada = new PersonalidadeNormalizada(this.personalidade);
            }
        }

        public String getApelido() {
            return apelido;
        }


        public Long getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public Personalidade getPersonalidade() {
            return personalidade;
        }

        public PersonalidadeNormalizada getPersonalidadeNormalizada() {
            return personalidadeNormalizada;
        }
    }

    public class AdminRepublica {
        Long id;
        String nome;
        Personalidade personalidade;
        List<Estudante> moradores;
        List<Estudante> pendentes;
        PersonalidadeNormalizada personalidadeNormalizada;

        public AdminRepublica(Republica rep) {
            this.id = rep.getId();
            this.nome = rep.getNome();
            this.personalidade = rep.getPerfil().getPersonalidade();
            this.moradores = new ArrayList<>();
            this.pendentes = new ArrayList<>();

            rep.getMoradores().forEach(m -> {
                if (m.isAprovado()) moradores.add(m.getMorador());
                else pendentes.add(m.getMorador());
            });

            this.personalidadeNormalizada = new PersonalidadeNormalizada(personalidade);
        }

        public String getNome() {
            return nome;
        }

        public Personalidade getPersonalidade() {
            return personalidade;
        }

        public List<Estudante> getMoradores() {
            return moradores;
        }

        public List<Estudante> getPendentes() {
            return pendentes;
        }

        public Long getId() {
            return id;
        }

        public PersonalidadeNormalizada getPersonalidadeNormalizada() {
            return personalidadeNormalizada;
        }
       }
    public class PersonalidadeNormalizada {
        float abertura;
        float concordancia;
        float consciencia;
        float extroversao;
        float neuroticismo;

        public PersonalidadeNormalizada(Personalidade personalidade) {
            if (personalidade != null) {
                DecimalFormat df= new DecimalFormat("##.00");

                this.abertura = Float.parseFloat(df.format(personalidade.getAbertura() / 50.00 * 100));
                this.concordancia = Float.parseFloat(df.format(personalidade.getConcordancia() / 45.00 * 100));
                this.consciencia = Float.parseFloat(df.format(personalidade.getConsciencia() / 45.00 * 100));
                this.extroversao = Float.parseFloat(df.format(personalidade.getExtroversao() / 40.00 * 100));
                this.neuroticismo = Float.parseFloat(df.format(personalidade.getNeuroticismo() / 40.00 * 100));
            }
        }

        public float getAbertura() {
            return abertura;
        }

        public float getConcordancia() {
            return concordancia;
        }

        public float getConsciencia() {
            return consciencia;
        }

        public float getExtroversao() {
            return extroversao;
        }

        public float getNeuroticismo() {
            return neuroticismo;
        }
    }

    public class StatePayload {
        int cadastros,republicas, estudantes, estudantes_aprovados, estudantes_pendentes, estudantes_sem_rep;

        public int getCadastros() {
            return cadastros;
        }

        public int getRepublicas() {
            return republicas;
        }

        public int getEstudantes() {
            return estudantes;
        }

        public int getEstudantes_sem_rep() {
            return estudantes_sem_rep;
        }

        public int getEstudantes_aprovados() {
            return estudantes_aprovados;
        }

        public int getEstudantes_pendentes() {
            return estudantes_pendentes;
        }
    }
}
